package gov.pnnl.goss.gridappsd;

import org.slf4j.Logger;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.jms.Destination;

import static gov.pnnl.goss.gridappsd.TestConstants.*;

import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import gov.pnnl.goss.gridappsd.api.DataManager;
import gov.pnnl.goss.gridappsd.api.StatusReporter;
import gov.pnnl.goss.gridappsd.configuration.ConfigurationManagerImpl;
import gov.pnnl.goss.gridappsd.data.GridAppsDataSourcesImpl;
import pnnl.goss.core.server.DataSourceBuilder;
import pnnl.goss.core.server.DataSourceObject;
import pnnl.goss.core.server.DataSourcePooledJdbc;
import pnnl.goss.core.server.DataSourceRegistry;
import pnnl.goss.server.registry.DataSourceRegistryImpl;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationManagerComponentTests {
	
	@Mock
	Logger logger;
	
	
	@Mock
	private StatusReporter statusReporter;
	
	@Mock
	private DataManager dataManager;
	
	@Captor
	ArgumentCaptor<String> argCaptor;
	
	@Mock DataSourcePooledJdbc datasourceObject;
	
		
	
	
	//report status on start
	@Test
	public void statusReportedWhen_configManagerStarted() {
		ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
		ConfigurationManagerImpl configManager = new ConfigurationManagerImpl(statusReporter, dataManager);
		configManager.start();
		Mockito.verify(statusReporter).reportStatus(argCaptor.capture());
		assertEquals("Starting gov.pnnl.goss.gridappsd.configuration.ConfigurationManagerImpl", argCaptor.getValue());
		
	}
	
	//test updated and get configuration property
	@Test
	public void configPropertiesSetWhen_configManagerUpdated() {
		ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
		ConfigurationManagerImpl configManager = new ConfigurationManagerImpl(statusReporter, dataManager);
		configManager.start();
		
		final String FNCS_PATH_PROP = "fncs.path";
		final String FNCS_PATH_VAL = "fncs_broker";
		final String GRIDLABD_PATH_PROP = "gridlabd.path";
		final String GRIDLABD_PATH_VAL = "gridlabd";
		final String GRIDAPPSD_PATH_PROP = "gridappsd.temp.path";
		final String GRIDAPPSD_PATH_VAL = "\\tmp\\gridappsd_tmp";
		final String FNCS_BRIDGE_PATH_PROP = "fncs.bridge.path";
		final String FNCS_BRIDGE_PATH_VAL = ".\\scripts\\goss_fncs_bridge.py";

		
		
		
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put(FNCS_PATH_PROP, FNCS_PATH_VAL);
		props.put(GRIDLABD_PATH_PROP, GRIDLABD_PATH_VAL);
		props.put(GRIDAPPSD_PATH_PROP, GRIDAPPSD_PATH_VAL);
		props.put(FNCS_BRIDGE_PATH_PROP, FNCS_BRIDGE_PATH_VAL);
		configManager.updated(props);
		
		assertEquals(FNCS_PATH_VAL, configManager.getConfigurationProperty(FNCS_PATH_PROP));
		assertEquals(GRIDLABD_PATH_VAL, configManager.getConfigurationProperty(GRIDLABD_PATH_PROP));
		assertEquals(GRIDAPPSD_PATH_VAL, configManager.getConfigurationProperty(GRIDAPPSD_PATH_PROP));
		assertEquals(FNCS_BRIDGE_PATH_VAL, configManager.getConfigurationProperty(FNCS_BRIDGE_PATH_PROP));
	}
	
	
	//get simulation file
	
	
	
//	@Test
//	/**
//	 * 	Succeeds when the proper number of properties are set on the updated call, and datasourcebuilder.create is called, and the correct registered datasource name is added
//	 */
//	public void registryUpdatedWhen_dataSourcesStarted(){
//		ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
//		Properties datasourceProperties = new Properties();
//		GridAppsDataSourcesImpl dataSources = new GridAppsDataSourcesImpl(logger, datasourceBuilder, datasourceRegistry, datasourceProperties);
//		Hashtable<String, String> props = new Hashtable<String, String>();
//		String datasourceName = "pnnl.goss.sql.datasource.gridappsd";
//		props.put("name", datasourceName);
//		props.put(DataSourceBuilder.DATASOURCE_USER, "gridappsduser");
//		props.put(DataSourceBuilder.DATASOURCE_PASSWORD, "gridappsdpw");
//		props.put(DataSourceBuilder.DATASOURCE_URL, "mysql://lalala");
//		props.put("driver", "com.mysql.jdbc.Driver");
//		dataSources.updated(props);
//		
//		assertEquals(5, datasourceProperties.size());
//		dataSources.start();
//
//		//verify datasourceBuilder.create(datasourceName, datasourceProperties);
//		try {
//			Mockito.verify(datasourceBuilder).create(argCaptor.capture(), Mockito.any());
//			assertEquals(datasourceName, argCaptor.getValue());
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			assert(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//			assert(false);
//		}
//		  
//		//verify registeredDatasources.add(datasourceName);
//		List<String> registeredDatasources = dataSources.getRegisteredDatasources();
//		assertEquals(1, registeredDatasources.size());
//		
//	}
//	
//	@Test
//	/**
//	 * 	Succeeds when the registry is empty after the service has been stopped
//	 */
//	public void registryClearedWhen_dataSourcesStopped(){
//		ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
//		Properties datasourceProperties = new Properties();
//		GridAppsDataSourcesImpl dataSources = new GridAppsDataSourcesImpl(logger, datasourceBuilder, datasourceRegistry, datasourceProperties);
//		Hashtable<String, String> props = new Hashtable<String, String>();
//		String datasourceName = "pnnl.goss.sql.datasource.gridappsd";
//		props.put("name", datasourceName);
//		props.put(DataSourceBuilder.DATASOURCE_USER, "gridappsduser");
//		props.put(DataSourceBuilder.DATASOURCE_PASSWORD, "gridappsdpw");
//		props.put(DataSourceBuilder.DATASOURCE_URL, "mysql://lalala");
//		props.put("driver", "com.mysql.jdbc.Driver");
//		dataSources.updated(props);
//		
//		assertEquals(5, datasourceProperties.size());
//		dataSources.start();
//
//		//verify datasourceBuilder.create(datasourceName, datasourceProperties);
//		try {
//			Mockito.verify(datasourceBuilder).create(argCaptor.capture(), Mockito.any());
//			assertEquals(datasourceName, argCaptor.getValue());
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			assert(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//			assert(false);
//		}
//		  
//		//verify registeredDatasources.add(datasourceName);
//		List<String> registeredDatasources = dataSources.getRegisteredDatasources();
//		assertEquals(1, registeredDatasources.size());
//		
//		
//		dataSources.stop();
//		
//		
//		assertEquals(0, dataSources.getRegisteredDatasources().size());
//		
//	}
//	
//	
//	@Test
//	/**
//	 * 	Succeeds when there was an error because no properties were passed in
//	 */
//	public void errorWhen_dataSourcesStartedWithNoProperties(){
//		ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
//		Properties datasourceProperties = new Properties();
//		GridAppsDataSourcesImpl dataSources = new GridAppsDataSourcesImpl(logger, datasourceBuilder, datasourceRegistry, datasourceProperties);
//		
//		try{
//			dataSources.start();
//		}catch(Exception e){
//			assertEquals("No datasource name provided when registering data source", e.getMessage());
//		}
//
//		
//	}	
//	
//	
//	
//	@Test
//	/**
//	 * 	Succeeds when the proper number of properties are set on the updated call, and datasourcebuilder.create is called, and the correct registered datasource name is added
//	 */
//	public void registryKeysExistWhen_dataSourcesStarted(){
//		ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
//
//		try {
//			//When datasourceBuilder.create is called add a face datasource object to the datasourceRegistry (similar to what the actual implementation would do)
//			Answer answer = new Answer() {
//				@Override
//				public Object answer(InvocationOnMock invocation) throws Throwable {
//					Object[] args = invocation.getArguments();
//					String dsName = args[0].toString();
//					datasourceRegistry.add(dsName, datasourceObject);
//					return null;
//				}
//			};
//			Mockito.doAnswer(answer).when(datasourceBuilder).create(argCaptor.capture(), Mockito.any());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		
//		
//		Properties datasourceProperties = new Properties();
//		GridAppsDataSourcesImpl dataSources = new GridAppsDataSourcesImpl(logger, datasourceBuilder, datasourceRegistry, datasourceProperties);
//		Hashtable<String, String> props = new Hashtable<String, String>();
//		String datasourceName = "pnnl.goss.sql.datasource.gridappsd";
//		props.put("name", datasourceName);
//		props.put(DataSourceBuilder.DATASOURCE_USER, "gridappsduser");
//		props.put(DataSourceBuilder.DATASOURCE_PASSWORD, "gridappsdpw");
//		props.put(DataSourceBuilder.DATASOURCE_URL, "mysql://lalala");
//		props.put("driver", "com.mysql.jdbc.Driver");
//		dataSources.updated(props);
//		
//		assertEquals(5, datasourceProperties.size());
//		dataSources.start();
//
//		//verify datasourceBuilder.create(datasourceName, datasourceProperties);
//		try {
//			Mockito.verify(datasourceBuilder).create(argCaptor.capture(), Mockito.any());
//			assertEquals(datasourceName, argCaptor.getValue());
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			assert(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//			assert(false);
//		}
//		  
//		//verify registeredDatasources.add(datasourceName);
//		List<String> registeredDatasources = dataSources.getRegisteredDatasources();
//		assertEquals(1, registeredDatasources.size());
//		
//		
//		//  test get data source keys
//		Collection<String> dsKeys = dataSources.getDataSourceKeys();
//		assertEquals(datasourceName, dsKeys.toArray()[0]);
//		
//		// test get data source by key
//		DataSourcePooledJdbc obj = dataSources.getDataSourceByKey(datasourceName);
//		assertEquals(datasourceObject, obj);
//		
//		
//		
//		// test get connection by key
//		dataSources.getConnectionByKey(datasourceName);
//		try {
//			Mockito.verify(datasourceObject).getConnection();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		
//		//verify datasourceregistry size
//		assertEquals(1, datasourceRegistry.getAvailable().size());
//		
//		
//	}

}
