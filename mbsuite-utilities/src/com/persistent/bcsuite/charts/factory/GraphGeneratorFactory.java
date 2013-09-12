package com.persistent.bcsuite.charts.factory;

import java.util.Map;

import org.apache.log4j.Logger;

import com.persistent.bcsuite.charts.constants.BCSuiteConstants;
import com.persistent.bcsuite.charts.objects.GenericBarChart;
import com.persistent.bcsuite.charts.objects.GenericLineChart;
import com.persistent.bcsuite.charts.objects.GenericMultiBarChart;
import com.persistent.bcsuite.charts.objects.GenericMultiLineChart;
import com.persistent.bcsuite.charts.objects.GenericPieChart;
import com.persistent.bcsuite.charts.objects.GenericScatterChart;
import com.persistent.bcsuite.charts.objects.Graph;
import com.persistent.bcsuite.charts.utils.ReportUtils;

/**
 * GraphGeneratorFactory returns an instance of graph object based on the type of graph.
 * 
 */
public class GraphGeneratorFactory {
   private static final Logger logger = Logger.getLogger(GraphGeneratorFactory.class);
   private static GraphGeneratorFactory graphFactory;

   private GraphGeneratorFactory() {

   }

   public static GraphGeneratorFactory getInstance() {
      if (graphFactory == null)
         graphFactory = new GraphGeneratorFactory();
      return graphFactory;
   }

   public Graph getGraph(String strToken, String reportType) {
      Graph g = null;
      try {
         Map<String, String> reportTypes = ReportUtils.loadConfig(BCSuiteConstants.COLLECTOR_CONFIG_FILE);
         if ("line".equalsIgnoreCase(reportTypes.get(reportType))) {
            GenericLineChart genericLineChart = new GenericLineChart(reportType, strToken);
            g = genericLineChart;
         } else if ("bar".equalsIgnoreCase(reportTypes.get(reportType))) {
            GenericBarChart genericBarChart = new GenericBarChart(reportType, strToken);
            g = genericBarChart;
         }else if ("pie".equalsIgnoreCase(reportTypes.get(reportType))) 
         {
            GenericPieChart genericPieChart = new GenericPieChart(reportType, strToken);
            g = genericPieChart;
         }
         else if ("scatter".equalsIgnoreCase(reportTypes.get(reportType))) 
         {
            GenericScatterChart genericScatterChart = new GenericScatterChart(reportType, strToken);
            g = genericScatterChart;
         }
         else if ("multiline".equalsIgnoreCase(reportTypes.get(reportType))) 
         {
            GenericMultiLineChart genericMultiLineChart = new GenericMultiLineChart(reportType, strToken);
            g = genericMultiLineChart;
         }
         else if ("multibar".equalsIgnoreCase(reportTypes.get(reportType))) 
         {
            GenericMultiBarChart genericMultiBarChart = new GenericMultiBarChart(reportType, strToken);
            g = genericMultiBarChart;
         }
         else
         {
            logger.error("Cannot generate graph for report type[" + reportType +"]. Dont know what type graph it is, skipping it");
         }
         
         if(g != null)
         {
            logger.info("From the provided list,plotting graph for ["+reportType +"] with token ["+strToken +"]");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return g;
   }

 

}
