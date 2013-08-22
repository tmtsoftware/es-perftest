package com.persistent.bcsuite.charts.objects;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import com.persistent.bcsuite.charts.constants.BCSuiteConstants;
import com.persistent.bcsuite.charts.graphs.GraphTypes;
import com.persistent.bcsuite.charts.utils.MySqlDAO;
import com.persistent.bcsuite.charts.utils.ReportUtils;

/**
 * Generates a Pie Chart for the given set of values. Saves the chart to a file.
 * Generates the HTML table for the data values
 * 
 */
public class GenericPieChart implements Graph {
   private static final Logger logger = Logger.getLogger(GenericPieChart.class);
   Map<String, Double> values =null;
   private String filename;
   private GraphTypes graphType;
   private String reportType;
   private String token;
   private String title;
   private static Random rand = new Random();

   public GenericPieChart(String reportType,String token) {
      this.token = token;
      this.reportType = reportType;
      this.filename = reportType+BCSuiteConstants.FILE_TYPE_EXTN_PNG;
   }

  
   /**
    * Method to plot the graph from the given data Saves the chart to a PNG
    * file.
    * 
    * @throws IOException
    * @throws SQLException
    * @throws ClassNotFoundException
    */
   @SuppressWarnings("deprecation")
   @Override
   public void plot() throws IOException, ClassNotFoundException, SQLException {
      PieDataset dataset = createDataset();
      if(dataset != null)
      {
         JFreeChart chart = ChartFactory.createPieChart(this.title, dataset,
               true, true, false);
         PiePlot plot = (PiePlot) chart.getPlot();
         plot.setSectionPaint(1,getColor());
         plot.setSectionPaint(2,getColor());
         plot.setBackgroundPaint(getPlotBackgroundPaint());
         PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
                  "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
              plot.setLabelGenerator(gen);
         ChartUtilities.saveChartAsPNG(getFile(), chart,
               BCSuiteConstants.CHART_WIDTH, BCSuiteConstants.CHART_HEIGHT);
      }
   }

   private PieDataset createDataset() throws IOException,
         ClassNotFoundException, SQLException {
      MySqlDAO dao = new MySqlDAO();
      dao.init();
      try{
      Map<String, Object> multiValueMap = dao.getGenericPieValueSet(reportType,token);
      values = (Map<String, Double>)multiValueMap.get("data");
      if(values == null)
      {
         logger.error("No values for Pie Chart");
         return null;
      }
      title = (String)multiValueMap.get("title");
      DefaultPieDataset pieDataSet = new DefaultPieDataset();
      if (values != null) {
         Set<String> keySet = values.keySet();
         for (String key : keySet) {
            Double value = values.get(key);
            pieDataSet.setValue(key, value.doubleValue());
         }
         return pieDataSet;
      }
      }catch(Exception e)
      {
         logger.error("Exception in GenericPieChart " + e);
      }
      finally
      {
         dao.cleanUp();
      }
      return null;
   }

   /**
    * Method for generating the HTML table for the data values in the graphs
    * 
    * @throws IOException
    */
   @Override
   public String generateTable() throws IOException {

      StringBuilder strBud = new StringBuilder();
      // main table td open
      strBud.append(BCSuiteConstants.TD_OPEN);
      strBud.append("<h1>");
      strBud.append(this.title);
      strBud.append("</h1>");
      strBud.append(BCSuiteConstants.TABLE_REPORT_OPEN);
      strBud.append(BCSuiteConstants.TH_OPEN);
      strBud.append(this.title);
      strBud.append(BCSuiteConstants.TH_CLOSE);
      if (values != null) {
         Set<String> mapKeySet = values.keySet();
         for (String mapKey : mapKeySet) {
            strBud.append(BCSuiteConstants.TR_OPEN);
            strBud.append(BCSuiteConstants.TD_OPEN);
            strBud.append(mapKey);
            strBud.append(BCSuiteConstants.TD_CLOSE);
            strBud.append(BCSuiteConstants.TD_OPEN);
            strBud.append(values.get(mapKey));
            strBud.append(BCSuiteConstants.TD_CLOSE);
            strBud.append(BCSuiteConstants.TR_CLOSE);

         }
      }
      strBud.append(BCSuiteConstants.TABLE_CLOSE);
      // main table td close
      strBud.append(BCSuiteConstants.TD_CLOSE);
      // main table td open for appending image
      strBud.append(BCSuiteConstants.TD_OPEN);
      strBud.append("<img src=\"" + token
            + BCSuiteConstants.FOLDER_PATH_IMAGE + filename
            + "\" alt=\"graph\"> ");
      strBud.append(BCSuiteConstants.TD_CLOSE);
      // main table td close for appending image
      List<String> machineStatsToAppend = ReportUtils
            .loadMachineStatGraphs(token);
      for (String strFileName : machineStatsToAppend) {
         strBud.append(BCSuiteConstants.TD_OPEN);
         strBud.append("<img src=\"" + token
               + BCSuiteConstants.FOLDER_PATH_IMAGE + strFileName
               + "\" alt=\"graph\"> ");
         strBud.append(BCSuiteConstants.TD_CLOSE);
      }

      return strBud.toString();

   }

   /**
    * Returns the file object to which the graph file is to be written.
    * 
    * @return - file object
    * @throws IOException
    */
   public File getFile() throws IOException {

      File file = new File(BCSuiteConstants.FOLDER_PATH_BCSUITE + token
            + BCSuiteConstants.FOLDER_PATH_IMAGE + this.filename);
      file.getParentFile().mkdirs();
      file.createNewFile();
      return file;

   }
   
   private Paint getColor()
   {
      int r = rand.nextInt(3) + rand.nextInt(4) + rand.nextInt(3);
      
      switch (r) {
      case 1:
         return Color.GREEN;
      case 2:
         return Color.MAGENTA;
      case 3:
         return Color.YELLOW;
      case 4 :
         return Color.CYAN;
      case 5 :
         return Color.ORANGE;
      case 6 :
         return Color.WHITE;
      case 7 :
         return Color.decode("#493D26");
      case 8 :
         return Color.decode("#9F000F");
      case 9 :
         return Color.decode("#571B7E");
      case 10 :
         return Color.BLUE;

      default:
         return Color.DARK_GRAY;
      }
   }
   
   private Paint getPlotBackgroundPaint()
   {
      int r = rand.nextInt(3) + rand.nextInt(4) + rand.nextInt(3);
      
      switch (r) {
      case 1:
         return Color.WHITE;
      case 2:
         return Color.BLACK;
      case 3:
         return Color.DARK_GRAY;
      case 4 :
         return Color.decode("#2C3539");
      case 5 :
         return Color.decode("#0000A0");
      case 6 :
         return Color.decode("#254117");
      case 7 :
         return Color.decode("#493D26");
      case 8 :
         return Color.decode("#9F000F");
      case 9 :
         return Color.decode("#571B7E");
      case 10 :
         return Color.BLUE;

      default:
         return Color.DARK_GRAY;
      }
      
   }

}
