package com.rashidmayes.bots.aerospike.impl;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public class Info extends CommandHandler {
	
	private ObjectMapper mObjectMapper = new ObjectMapper();
	private ObjectWriter mObjectWriter = mObjectMapper.writerWithDefaultPrettyPrinter();
	
	@Override
	public void execute(SlackMessagePosted event) {
		
		try {
			Map<String, Object> details = new HashMap<String, Object>();
			details.put("network", InetAddress.getLocalHost().toString());
			
			MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
			details.put("heapMemoryUsage", memoryMXBean.getHeapMemoryUsage());
			details.put("nonHeapMemoryUsage", memoryMXBean.getNonHeapMemoryUsage());

			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			details.put("startTime", runtimeMXBean.getStartTime());
			details.put("uptime", runtimeMXBean.getUptime());
			details.put("bootClassPath", runtimeMXBean.getBootClassPath());
			details.put("libraryPath", runtimeMXBean.getLibraryPath());
			details.put("managementSpecVersion", runtimeMXBean.getManagementSpecVersion());
			details.put("mname", runtimeMXBean.getName());
			details.put("specName", runtimeMXBean.getSpecName());
			details.put("specVendor", runtimeMXBean.getSpecVendor());
			details.put("specVersion", runtimeMXBean.getSpecVersion());
			details.put("vmName", runtimeMXBean.getVmName());
			details.put("vmVendor", runtimeMXBean.getVmVendor());
			details.put("vmVersion", runtimeMXBean.getVmVersion());
			
			OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
			details.put("availableProcessors", operatingSystemMXBean.getAvailableProcessors());
			details.put("systemLoadAverage", operatingSystemMXBean.getSystemLoadAverage());
			details.put("arch", operatingSystemMXBean.getArch());
			details.put("oname", operatingSystemMXBean.getName());

		    
		    ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
	        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
	        Thread[] threads = new Thread[threadGroup.activeCount()];
	        int total = threadGroup.enumerate(threads,false);
	        
	        for ( int i = 0; i < total; i++ ) {
	        	
	        	details.put("Thread"+i, 
	        			String.format(
	        					"%s, %s, %s, %s",
	        		        	threads[i].getName()
	        		        	,threads[i].getPriority()
	        		        	,threadBean.getThreadInfo(threads[i].getId()).getThreadState().name()
	        		        	,threadBean.getThreadCpuTime(threads[i].getId())
	        		        	)
	        	);
	        }
	        
	        details.put("threadCount", threadBean.getThreadCount());
	        details.put("totalStartedThreadCount", threadBean.getTotalStartedThreadCount());
	        details.put("peakThreadCount", threadBean.getPeakThreadCount());
	        details.put("currentThreadCpuTime", threadBean.getCurrentThreadCpuTime());
	        details.put("currentThreadUserTime", threadBean.getCurrentThreadUserTime());
	        details.put("daemonThreadCount", threadBean.getDaemonThreadCount());
	        
		    /*
	        long[] threadIds = threadBean.getAllThreadIds();
	        ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadIds, 0);
	        for ( ThreadInfo threadInfo : threadInfos ) {
	        	//
	        }*/
	        
			String json = mObjectWriter.writeValueAsString(details);
			this.mAgent.sendMessage(event.getSender(), "```"+json+"```");
		} catch (IOException e) {
			mLogger.severe(e.getMessage());
			this.mAgent.sendMessage(event.getSender(), e.getMessage());
		}
	}
	
	@Override
	public String getDescription() {
		return "Prints information about AeroSlack";
	}
}
