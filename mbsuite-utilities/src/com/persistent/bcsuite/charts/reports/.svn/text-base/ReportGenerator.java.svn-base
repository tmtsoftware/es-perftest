package com.persistent.bcsuite.charts.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.persistent.bcsuite.charts.constants.BCSuiteConstants;
import com.persistent.bcsuite.charts.factory.GraphGeneratorFactory;
import com.persistent.bcsuite.charts.graphs.GraphPlotter;
import com.persistent.bcsuite.charts.graphs.GraphTypes;
import com.persistent.bcsuite.charts.objects.Graph;
import com.persistent.bcsuite.charts.utils.ReportUtils;

/**
 * This class generates the HTML report from the statistics collected in the
 * database. It is also responsible for creating the required graphs by invoking
 * necessary classes.
 * 
 * 
 */
public class ReportGenerator {

	private final static Logger logger = Logger
			.getLogger(ReportGenerator.class);
	private static Connection connection;

	public static void main(String[] args) throws SQLException, IOException,
			ClassNotFoundException {
		// Get the graph type and token from user
		if (args.length == 0) {
		   System.out.println("\nERROR :: No arguments provided");
		   System.out.println("You need to provide arguments to this utility.");
		   System.out.println("This utility requires arguments in the format <report-type>:<data-token>, you can provide multiple combinations of <report-type>:<data-token> separated by commas");
		   System.out.println("Run this utility again with the switch -showtypes to see all the supported report types\n");
		   return;
		}
		else if(args.length == 1 && "-showtypes".equalsIgnoreCase(args[0]))
		{
	       Map<String,String> sqlQueryMap = ReportUtils.loadConfig("properties/queries.properties");
	         if(sqlQueryMap != null && sqlQueryMap.size() > 0)
	         {
	            System.out.println("\nThe utility supports the following report types currently:\n");
	            for (Map.Entry<String,String> entry : sqlQueryMap.entrySet()) {
	               String val = entry.getValue();
	               String s[] = val.split("#");
	               System.out.println("Report Type = " + entry.getKey()+ " , "+"Report Title = " + s[1]);
	           }
	           System.out.println("\nProvide appropriate tokens with each report type separated by a colon character ':'\n");
	         }
	         return;
		}
		logger.info("\n-----------------------Report Generator Started-----------------------\n");
		ReportGenerator repGen = new ReportGenerator();
		try {
			repGen.process(args[0].trim());
		} catch (Exception e) {
			logger.error("Exception in Processing Report", e);
		}
		repGen.cleanUp();
		logger.info("\n-----------------------Report Generator Completed-----------------------\n");
	}

	public void process(String strGraphTypesLst) throws Exception {
		GraphPlotter graphPlot = new GraphPlotter();
		ArrayList<Graph> graphs = new ArrayList<Graph>();
		// For each type of graph request run the loop
		GraphGeneratorFactory graphFactory = GraphGeneratorFactory
				.getInstance();
		String[] arrGraphTypes = ReportUtils.strSplit(strGraphTypesLst,
				BCSuiteConstants.COMMA);
		for (int i = 0; i < arrGraphTypes.length; i++) {
			String strGraphTypeToke = arrGraphTypes[i].trim();
			// Split the graph type into token and graph type
			String[] arrGTypeToken = ReportUtils.strSplit(strGraphTypeToke,
					BCSuiteConstants.COLON);
			if(arrGTypeToken.length <2)
			{
			   System.out.println("The part argument [" +strGraphTypeToke+"] is not correct. You might have missed to provide the token or the report type.");
			   continue;
			}
			String strGraphType = arrGTypeToken[0].trim();
			String token = arrGTypeToken[1].trim();
			Graph gp = graphFactory.getGraph(token,strGraphType);
			if(gp != null)
			graphs.add(gp);			
		}
		graphPlot.plotGraph(graphs);
		generateHTMLReport(graphs, BCSuiteConstants.REPORT_FILE_NAME);
	}

	/**
	 * Method that generates the HTML report.
	 * 
	 * @param graphs
	 *            - list of graphs to be added to the report.
	 * @param reportFileName
	 *            - name of the report file
	 * @throws IOException
	 */
	public void generateHTMLReport(List<Graph> graphs, String reportFileName)
			throws IOException {
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(
				ReportUtils.getFile(BCSuiteConstants.FOLDER_PATH_BCSUITE
						+ reportFileName)));
		StringBuilder strBud = new StringBuilder();
		strBud.append("<html><head><title> REPORT </title></head>");
		strBud.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"reportstyle.css\">");
		strBud.append("<body>");

		strBud.append(BCSuiteConstants.TABLE_REPORT_MAIN_OPEN);
		strBud.append(BCSuiteConstants.TR_OPEN);
		for (Graph g : graphs) {
			strBud.append(BCSuiteConstants.TD_OPEN);// Main table td
			strBud.append(g.generateTable());
			strBud.append(BCSuiteConstants.TD_CLOSE);// Main table td close
		}
		strBud.append(BCSuiteConstants.TR_CLOSE);
		strBud.append(BCSuiteConstants.TABLE_CLOSE);

		strBud.append("</body></html>");
		bw.write(strBud.toString());
		bw.close();
		
		//Copy the stylesheet also
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("reportstyle.css");
		File dest = new File(BCSuiteConstants.FOLDER_PATH_BCSUITE + "reportstyle.css");
		FileOutputStream out = new FileOutputStream(dest);
		try
	    {
	        try
	        {
	            final byte[] buffer = new byte[1024];
	            int n;
	            while ((n = in.read(buffer)) != -1)
	                out.write(buffer, 0, n);
	        }
	        finally
	        {
	            out.close();
	        }
	    }
	    finally
	    {
	        in.close();
	    }
		
		
	}

	/**
	 * Method to clean up after program completes. Closes the SQL data
	 * connections.
	 */
	public void cleanUp() {

		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			logger.error("Error closing database connection : ", e);
		}

	}
	
	

}
