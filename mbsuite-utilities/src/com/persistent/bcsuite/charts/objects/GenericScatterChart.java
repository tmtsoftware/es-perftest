package com.persistent.bcsuite.charts.objects;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.persistent.bcsuite.charts.constants.BCSuiteConstants;
import com.persistent.bcsuite.charts.utils.MySqlDAO;
import com.persistent.bcsuite.charts.utils.ReportUtils;

/**
 * Generates a Scatter Chart for the given set of values. Saves the chart to a file. Generates the HTML table for the
 * data values
 * 
 */
public class GenericScatterChart implements Graph {
   private static final Logger logger = Logger.getLogger(GenericScatterChart.class);
   HashMap<String, Double[][]> values;
   private String strToken;
   private String title;
   private String reportType;
   private String filename;
   private String xLabel;
   private String yLabel;

   public GenericScatterChart(String reportType,String token) {
      this.filename = reportType + BCSuiteConstants.FILE_TYPE_EXTN_PNG;
      this.strToken = token;
      this.reportType = reportType;
   }

   /**
    * Method to plot the graph from the given data. Saves the chart to a PNG file.
    * 
    * @throws IOException
    * @throws SQLException
    * @throws ClassNotFoundException
    */
   @Override
   public void plot() throws IOException, ClassNotFoundException, SQLException {
      XYDataset dataset = createDataset();
      JFreeChart chart = ChartFactory.createScatterPlot(this.title, this.xLabel, this.yLabel,dataset ,
               PlotOrientation.VERTICAL, true, true, false);
      XYPlot xyPlot = (XYPlot) chart.getPlot();
      /*
       * NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
       * 
       * domain.setTickUnit(new NumberTickUnit(100)); domain.setVerticalTickLabels(true); NumberAxis rangeAxis =
       * (NumberAxis) xyPlot.getRangeAxis(); rangeAxis.setTickUnit(new NumberTickUnit(500));
       */
      ChartUtilities.saveChartAsPNG(getFile(), chart, BCSuiteConstants.CHART_WIDTH, BCSuiteConstants.CHART_HEIGHT);
   }

   private XYDataset createDataset() throws IOException, ClassNotFoundException, SQLException {
      logger.info("Create data set called");
      HashMap<String, Double[][]> scatterValueMap = new HashMap<String, Double[][]>();
      MySqlDAO dao = new MySqlDAO();
      dao.init();
      logger.info("DAO inited");
      XYSeriesCollection result = new XYSeriesCollection();
      try {
         Map<String, Object> dmap = dao.getGenericSummaryValueSet(reportType, strToken);
         if(dmap == null || dmap.size() == 0)
         {
            logger.info("Cannot generate Scatter chart,some problem while executing query or no data returned");
            return null;
         }
         double[][] valuesArray = (double[][]) dmap.get("data");
         logger.info("value array = " + valuesArray);
         xLabel = (String) dmap.get("xLabel");
         yLabel = (String) dmap.get("yLabel");
         title = (String) dmap.get("title");

         Double[][] convertedArray = ReportUtils.convertdoubleToDouble(valuesArray);
         scatterValueMap.put(this.title,convertedArray );
         //values.put("valuesdata", convertedArray);
         Set<String> keySet = scatterValueMap.keySet();
        
         for (String key : keySet) {
            XYSeries series = new XYSeries(key);
            Double[][] arrValues = scatterValueMap.get(key);
            for (int i = 0; i < arrValues.length; i++) {
               int j = 0;
               series.add(arrValues[i][j], arrValues[i][j + 1]);
            }
            result.addSeries(series);
         }
      } catch (Exception e) {
         logger.error("Recieved exception in GenericScatterChart " + e);
         e.printStackTrace();
      } finally {
         dao.cleanUp();
      }

      return result;

   }

   /**
    * Get the valueset for chart
    * 
    * @return - Values for this chart
    */
   public HashMap<String, Double[][]> getValues() {
      return values;
   }

   /**
    * Method for generating the HTML table for the data values in the graphs
    * 
    * @throws IOException
    */
   @Override
   public String generateTable() throws IOException {

      StringBuilder strBud = new StringBuilder();
      strBud.append(BCSuiteConstants.TD_OPEN);
      strBud.append("<img src=\"" + strToken + BCSuiteConstants.FOLDER_PATH_IMAGE + filename + "\" alt=\"graph\"> ");
      strBud.append(BCSuiteConstants.TD_CLOSE);
      // main table td close for appending image
      List<String> machineStatsToAppend = ReportUtils.loadMachineStatGraphs(strToken);
      for (String strFileName : machineStatsToAppend) {
         strBud.append(BCSuiteConstants.TD_OPEN);
         strBud.append("<img src=\"" + strToken + BCSuiteConstants.FOLDER_PATH_IMAGE + strFileName
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

      File file = new File(BCSuiteConstants.FOLDER_PATH_BCSUITE + strToken + BCSuiteConstants.FOLDER_PATH_IMAGE
               + this.filename);
      file.getParentFile().mkdirs();
      file.createNewFile();
      return file;

   }

   public String getTitle() {
      return title;
   }

   public String getFilename() {
      return filename;
   }

   public String getxLabel() {
      return xLabel;
   }

   public String getyLabel() {
      return yLabel;
   }

   public String getStrToken() {
      return strToken;
   }

}
