package com.persistent.bcsuite.charts.objects;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

import com.persistent.bcsuite.charts.constants.BCSuiteConstants;
import com.persistent.bcsuite.charts.utils.MySqlDAO;

public class GenericMultiBarChart implements Graph {
      private static final Logger logger = Logger.getLogger(GenericMultiBarChart.class);
      private double[][] tableVal;
      private String reportType;
      private String token;
      private String title;
      private String xLabel;
      private String yLabel;
      private String fileName;
      ArrayList<Map<String,Object>> valuesList=null;

   public GenericMultiBarChart(String rt,String t)
   {
      this.reportType = rt;
      this.token = t;
   }
   
   @Override
   public void plot() throws IOException, Exception {
      DefaultCategoryDataset dataset = createDataset();
      JFreeChart chart = ChartFactory.createBarChart(
               title, xLabel,yLabel,
               dataset, PlotOrientation.VERTICAL, true, true, false);
      final CategoryPlot plot = chart.getCategoryPlot();
      final CategoryAxis domainAxis = plot.getDomainAxis();
      domainAxis.setCategoryLabelPositions(
          CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 5.0));
      CategoryItemRenderer renderer = plot.getRenderer();
      ((BarRenderer)(renderer)).setItemMargin(0.0);
      LegendTitle legend = chart.getLegend();
      legend.setPosition(RectangleEdge.RIGHT);
      
      ChartUtilities.saveChartAsPNG(getFile(), chart,
            900, BCSuiteConstants.CHART_HEIGHT);
   }
   
   private DefaultCategoryDataset createDataset() throws IOException, ClassNotFoundException, SQLException {
      MySqlDAO dao = new MySqlDAO();
      dao.init();
      Map<String, Object> data = dao.getGenericMultiSummaryValueSet(reportType, token);
      if(data == null)
      {
         logger.error("Did not get any data from the DAO");
         return null;
      }
      title = (String) data.get("title");
      xLabel = (String) data.get("xLabel");
      yLabel = (String) data.get("yLabel");

      valuesList = (ArrayList<Map<String,Object>>)data.get("data");
      logger.info("ValuesList size = " + valuesList.size());      
      DefaultCategoryDataset d = new DefaultCategoryDataset();
      
      for (Map<String,Object> seriesMap: valuesList) {
         tableVal = (double[][])seriesMap.get("values");
         String t = (String)seriesMap.get("seriesTitle");

         logger.info("tableVal.length = " + tableVal.length);
         for (int i = 0; i < tableVal.length; i++) {
            int j = 0;
            d.addValue(tableVal[i][j+1],t,String.valueOf((int)tableVal[i][j]));
         }         

      }      
      dao.cleanUp();
      return d;
   }
   
   
   private JFreeChart createChart(final CategoryDataset dataset) {
      
      // create the chart...
      final JFreeChart chart = ChartFactory.createBarChart(
          "Bar Chart Demo",         // chart title
          "Category",               // domain axis label
          "Value",                  // range axis label
          dataset,                  // data
          PlotOrientation.VERTICAL, // orientation
          true,                     // include legend
          true,                     // tooltips?
          false                     // URLs?
      );

      // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

      // set the background color for the chart...
      chart.setBackgroundPaint(Color.white);

      // get a reference to the plot for further customisation...
      final CategoryPlot plot = chart.getCategoryPlot();
      plot.setBackgroundPaint(Color.lightGray);
      plot.setDomainGridlinePaint(Color.white);
      plot.setRangeGridlinePaint(Color.white);

      // set the range axis to display integers only...
      final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
      rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

      // disable bar outlines...
      final BarRenderer renderer = (BarRenderer) plot.getRenderer();
      renderer.setDrawBarOutline(false);
      
      // set up gradient paints for series...
      final GradientPaint gp0 = new GradientPaint(
          0.0f, 0.0f, Color.blue, 
          0.0f, 0.0f, Color.lightGray
      );
      final GradientPaint gp1 = new GradientPaint(
          0.0f, 0.0f, Color.green, 
          0.0f, 0.0f, Color.lightGray
      );
      final GradientPaint gp2 = new GradientPaint(
          0.0f, 0.0f, Color.red, 
          0.0f, 0.0f, Color.lightGray
      );
      final GradientPaint gp3 = new GradientPaint(
               0.0f, 0.0f, Color.YELLOW, 
               0.0f, 0.0f, Color.lightGray
           );
      renderer.setSeriesPaint(0, gp0);
      renderer.setSeriesPaint(1, gp1);
      renderer.setSeriesPaint(2, gp2);
      renderer.setSeriesPaint(3, gp3);
      renderer.setSeriesPaint(3, gp3);

      final CategoryAxis domainAxis = plot.getDomainAxis();
      domainAxis.setCategoryLabelPositions(
          CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
      );
      // OPTIONAL CUSTOMISATION COMPLETED.
      
      return chart;
      
  }
//   private DefaultCategoryDataset createDataset() throws IOException,
//   ClassNotFoundException, SQLException {
//         MySqlDAO dao = new MySqlDAO();
//         dao.init();
//         Map<String,Object> data = dao.getGenericSummaryValueSet(reportType,token);
//         title = (String)data.get("title");
//         xLabel = (String)data.get("xLabel");
//         yLabel = (String)data.get("yLabel");
//         tableVal = (double[][])data.get("data");
//         
//         DefaultCategoryDataset d = new DefaultCategoryDataset();
//         
//         for (int i = 0; i < tableVal.length; i++) {
//            int j = 0;
//            d.setValue(tableVal[i][j++],yLabel,String.valueOf(tableVal[i][j]));
//         }        
//         dao.cleanUp();
//         return d;
//}
   
   /**
    * Returns the file object to which the graph file is to be written.
    * 
    * @return - file object
    * @throws IOException
    */
   public File getFile() throws IOException {
      fileName = BCSuiteConstants.FOLDER_PATH_BCSUITE + token
      + BCSuiteConstants.FOLDER_PATH_IMAGE + this.reportType+".png";
      File file = new File(fileName);
      file.getParentFile().mkdirs();
      file.createNewFile();
      return file;
   }

   
   @Override
   public String generateTable() throws IOException {
      StringBuilder strBud = new StringBuilder();
      // main table td open
      
      strBud.append(BCSuiteConstants.TD_OPEN);
      strBud.append("<h1>");
      strBud.append(this.title);
      strBud.append("</h1>");
      for (Map<String,Object> seriesMap: valuesList) {
         tableVal = (double[][])seriesMap.get("values");
         String t = (String)seriesMap.get("seriesTitle");
         strBud.append("<h1>");
         strBud.append(t);
         strBud.append("</h1>");
         strBud.append(BCSuiteConstants.TABLE_REPORT_OPEN);
         strBud.append(BCSuiteConstants.TH_OPEN);
         strBud.append(xLabel);
         strBud.append(BCSuiteConstants.TH_CLOSE);
         strBud.append(BCSuiteConstants.TH_OPEN);
         strBud.append(yLabel);
         strBud.append(BCSuiteConstants.TH_CLOSE);
         for (int i = 0; i < tableVal.length; i++) {
            strBud.append(BCSuiteConstants.TR_OPEN);
            int j = 0;
            strBud.append(BCSuiteConstants.TD_OPEN);
            strBud.append(tableVal[i][j]);
            strBud.append(BCSuiteConstants.TD_CLOSE);
            strBud.append(BCSuiteConstants.TD_OPEN);
            strBud.append(tableVal[i][j + 1]);
            strBud.append(BCSuiteConstants.TD_CLOSE);
            strBud.append(BCSuiteConstants.TR_CLOSE);            
         }         
         strBud.append(BCSuiteConstants.TABLE_CLOSE);
      }   
      strBud.append(BCSuiteConstants.TD_CLOSE);
      // main table td close
      // main table td open for appending image
      strBud.append(BCSuiteConstants.TD_OPEN);
      strBud.append("<img src=\"" + token
               + BCSuiteConstants.FOLDER_PATH_IMAGE + this.reportType+".png"
            + "\" alt=\"graph\"> ");
      strBud.append(BCSuiteConstants.TD_CLOSE);

      return strBud.toString();
   }

}
