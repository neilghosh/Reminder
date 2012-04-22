package org.iiit;

import java.io.IOException;
import javax.servlet.http.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.iiit.Reminder;
import org.iiit.PMF;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ReminderServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		String query = "select from " + Reminder.class.getName()
				+ " where deleted == False order by when ";
		List<Reminder> reminders = (List<Reminder>) pm.newQuery(query)
				.execute();

		resp.setContentType("text/html");

		StringBuffer reminderTable = new StringBuffer();
		String style = "<style type=text/css>	" +
				"	tr.datacell1 {			background-color: #DDDDDD; color: black;		}	" +
				"	tr.datacell2 {			background-color: #FFFFFF; color: black;		}		" +
				"</style>";
		reminderTable.append(style);
		reminderTable.append("<hr><H3>Add reminder</h3>");

		reminderTable
				.append("<form action=reminder?action=insert method=post >"
						
						+ "Date(dd/MM/yyyy) <input name=date1  type=text value=30/12/2010>&nbsp;"
						+ "Time(HH24:mm:ss) <input name=time1  type=text value=11:56:56><br>"
						+ "Subject <input name=what  type=text size=100  value=TODO>"
						+ "<input  type=submit value=Add>" + "</form>");

		Iterator<Reminder> it = reminders.iterator();
		if (!(it.hasNext()))
			reminderTable.append("<h3>No reminders available !</h3>");
		else {
			reminderTable.append("<hr><h3>List of existing reminders </h3>");

			reminderTable
					.append("<form action=reminder?action=delete method=post>");

			reminderTable.append("<table border=0>");
			reminderTable
					.append("<tr bgcolor=#AAAAAA><th>Reminder ID</th><th>Due time</th><th width=400>Agenda </th><th>User</th><th>Delete</th></tr>");
			int rowcount = 0;
			while (it.hasNext()) {
				rowcount++;
				Reminder reminder = it.next();
				reminderTable.append("<tr class=datacell"+rowcount%2+" >");
				reminderTable.append("<td>");
				reminderTable.append(reminder.getKey().getId());
				reminderTable.append("</td>");
				reminderTable.append("<td>");
				SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
				String formattedDate = formatter.format(reminder.getWhen());
				reminderTable.append(formattedDate);
				reminderTable.append("</td>");
				reminderTable.append("<td width=400>");
				reminderTable.append(reminder.getWhat());
				reminderTable.append("</td>");
				reminderTable.append("<td>");
				reminderTable.append(reminder.getUsername());
				reminderTable.append("</td>");

				reminderTable.append("<td>");
				reminderTable.append("<input type=checkbox name=del value="
						+ reminder.getKey().getId() + ">");
				reminderTable.append("</td>");
				reminderTable.append("<td>");

				reminderTable.append("</td>");

				reminderTable.append("</tr>");
			}

			reminderTable.append("</table>");
			reminderTable.append("<input type=submit value=Delete>");
			reminderTable.append("<form>");
		}
		resp.getWriter().println(reminderTable);

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String msg = "";
		PersistenceManager pm = PMF.get().getPersistenceManager();

		resp.setContentType("text/html");
		// resp.getWriter().println("Action = " + req.getParameter("action"));
		// resp.getWriter().print(" key = " + req.getParameter("key"));

		if (req.getParameter("action").equals("delete")) {

			String select[] = req.getParameterValues("del");
			if (select != null && select.length != 0) {
				// resp.getWriter().println("You have selected: ");
				for (int i = 0; i < select.length; i++) {
					// resp.getWriter().println(select[i]);

					Key k = KeyFactory.createKey(
							Reminder.class.getSimpleName(), Long
									.parseLong(select[i]));
					Reminder e = pm.getObjectById(Reminder.class, k);
					 
					//e.setDeleted(true);
					//pm.makePersistent(e);
					//pm.close(); 
					pm.deletePersistent(e);

				}
				msg = msg + "<br>" + select.length + " reminder(s) deleted ";
			} else {
				msg = msg + "Select at least one reminder to delete<br>";
			}

		}
		if (req.getParameter("action").equals("insert")) {

			boolean valid = true;
			String what = req.getParameter("what");
			if (what.equals("")) {
				msg = msg + "Error: Enter agenda<br> ";
				valid = false;
			}
			String date1 = req.getParameter("date1");
			if (date1.equals("")) {
				msg = msg + "Error: Enter date<br> ";
				valid = false;
			}
			String time1 = req.getParameter("time1");
			if (time1.equals("")) {
				msg = msg + "Error: Enter time<br> ";
				valid = false;
			}

			DateFormat df = new SimpleDateFormat("dd/MM/yyyyHH:mm:ss");
			Date today = new Date();
			try {
				today = df.parse(date1 + time1);
				System.out.println("Today = " + df.format(today));
			} catch (ParseException e) {
				msg = msg + " Invalida Date/time";
				valid = false;
				// e.printStackTrace();
			}
			if (valid) {
				 String username="";
				 if (req.getUserPrincipal() != null) {
					 username = req.getUserPrincipal().getName();
				 }
				 else
				 {
					 username = req.getRemoteHost();
				 }

				Reminder reminder = new Reminder(today, username, what, false);

				try {
					pm.makePersistent(reminder);
					msg = msg
							+ ("Reminder Added for <b>" + what + "</b> at "
									+ today.toString() + "<br>");
				} finally {
					pm.close();
				}
			}
		}

		resp.getWriter().println(msg);
		doGet(req, resp);

	}

}
