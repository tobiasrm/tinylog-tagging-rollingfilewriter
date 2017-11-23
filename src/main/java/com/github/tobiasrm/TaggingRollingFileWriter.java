package com.github.tobiasrm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.EnvironmentHelper;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.labelers.CountLabeler;
import org.pmw.tinylog.labelers.Labeler;
import org.pmw.tinylog.policies.Policy;
import org.pmw.tinylog.policies.StartupPolicy;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.PropertiesSupport;
import org.pmw.tinylog.writers.Property;
import org.pmw.tinylog.writers.VMShutdownHook;
import org.pmw.tinylog.writers.Writer;

/**
 * Writes log entries to a file like {@link org.pmw.tinylog.writers.FileWriter FileWriter} but keeps backups of old
 * logging files.
 */
@PropertiesSupport(name = "tagging-rollingfilewriter", properties = { 
		@Property(name = "filename",      type = String.class), 
		@Property(name = "backups",       type = int.class),
		@Property(name = "buffered",      type = boolean.class, optional = true),
		@Property(name = "label",         type = Labeler.class, optional = true),
		@Property(name = "customTag1",    type = String.class, optional = true),
		@Property(name = "customTag2",    type = String.class, optional = true),
		@Property(name = "customTag3",    type = String.class, optional = true),
		@Property(name = "customTag4",    type = String.class, optional = true),
		@Property(name = "customTag5",    type = String.class, optional = true),
		@Property(name = "customTag6",    type = String.class, optional = true),
		@Property(name = "customTag7",    type = String.class, optional = true),
		@Property(name = "customTag8",    type = String.class, optional = true),
		@Property(name = "customTag9",    type = String.class, optional = true),
		@Property(name = "customTag10",   type = String.class, optional = true),
		@Property(name = "customParam1",  type = String.class, optional = true),
		@Property(name = "customParam2",  type = String.class, optional = true),
		@Property(name = "customParam3",  type = String.class, optional = true),
		@Property(name = "customParam4",  type = String.class, optional = true),
		@Property(name = "customParam5",  type = String.class, optional = true),
		@Property(name = "customParam6",  type = String.class, optional = true),
		@Property(name = "customParam7",  type = String.class, optional = true),
		@Property(name = "customParam8",  type = String.class, optional = true),
		@Property(name = "customParam9",  type = String.class, optional = true),
		@Property(name = "customParam10", type = String.class, optional = true),
		@Property(name = "policies",      type = Policy[].class, optional = true)  
})
public final class TaggingRollingFileWriter implements Writer {

	private static final int BUFFER_SIZE = 64 * 1024;

	private final String filename;
	private final int backups;
	private final boolean buffered;
	private final Labeler labeler;
	private final List<? extends Policy> policies;

	private final Object mutex;
	private File file;
	private OutputStream stream;

	// --------------------------------------------------------------------

	private final String customTag1;
	private final String customTag2;
	private final String customTag3;
	private final String customTag4;
	private final String customTag5;
	private final String customTag6;
	private final String customTag7;
	private final String customTag8;
	private final String customTag9;
	private final String customTag10;

	private final String customParam1;
	private final String customParam2;
	private final String customParam3;
	private final String customParam4;
	private final String customParam5;
	private final String customParam6;
	private final String customParam7;
	private final String customParam8;
	private final String customParam9;
	private final String customParam10;

	String l;

	// --------------------------------------------------------------------

	/**
	 * Rolling log files once at startup.
	 *
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 *
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public TaggingRollingFileWriter(final String filename, final int backups) {
		this(filename, backups, false, null, 
				null,null,null,null,null,null,null,null,null,null,
				null,null,null,null,null,null,null,null,null,null,
				(Policy[]) null );
	}

	/**
	 * Rolling log files once at startup.
	 *
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 *
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public TaggingRollingFileWriter(final String filename, final int backups, final boolean buffered) {
		this(filename, backups, buffered, null, 
				null,null,null,null,null,null,null,null,null,null,
				null,null,null,null,null,null,null,null,null,null,
				(Policy[]) null );
	}

	/**
	 * Rolling log files once at startup.
	 *
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param labeler
	 *            Labeler for naming backups
	 *
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public TaggingRollingFileWriter(final String filename, final int backups, final Labeler labeler) {
		this(filename, backups, false, labeler,  
				null,null,null,null,null,null,null,null,null,null,
				null,null,null,null,null,null,null,null,null,null,
				(Policy[]) null );
	}

	/**
	 * Rolling log files once at startup.
	 *
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param labeler
	 *            Labeler for naming backups
	 *
	 * @see org.pmw.tinylog.policies.StartupPolicy
	 */
	public TaggingRollingFileWriter(final String filename, final int backups, final boolean buffered, final Labeler labeler) {
		this(filename, backups, buffered, labeler, 
				null,null,null,null,null,null,null,null,null,null,
				null,null,null,null,null,null,null,null,null,null,
				(Policy[]) null );
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param policies
	 *            Rollover strategies
	 */
	public TaggingRollingFileWriter(final String filename, final int backups, final Policy... policies) {
		this(filename, backups, false, null, 
				null,null,null,null,null,null,null,null,null,null,
				null,null,null,null,null,null,null,null,null,null,
				policies);
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param policies
	 *            Rollover strategies
	 */
	public TaggingRollingFileWriter(final String filename, final int backups, final boolean buffered, final Policy... policies) {
		this(filename, backups, buffered, null,  
				null,null,null,null,null,null,null,null,null,null,
				null,null,null,null,null,null,null,null,null,null,
				policies );
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param labeler
	 *            Labeler for naming backups
	 * @param policies
	 *            Rollover strategies
	 */
	public TaggingRollingFileWriter(final String filename, final int backups, final Labeler labeler, final Policy... policies) {
		this(filename, backups, false, labeler,  
				null,null,null,null,null,null,null,null,null,null,
				null,null,null,null,null,null,null,null,null,null,
				policies );
	}

	/**
	 * @param filename
	 *            Filename of the log file
	 * @param backups
	 *            Number of backups
	 * @param buffered
	 *            Buffered writing
	 * @param labeler
	 *            Labeler for naming backups
	 * @param policies
	 *            Rollover strategies
	 */
	public TaggingRollingFileWriter(final String filename, final int backups, final boolean buffered, final Labeler labeler, 
			final String customTag1, 
			final String customTag2, 
			final String customTag3, 
			final String customTag4, 
			final String customTag5, 
			final String customTag6, 
			final String customTag7, 
			final String customTag8, 
			final String customTag9, 
			final String customTag10,
			final String customParam1, 
			final String customParam2, 
			final String customParam3, 
			final String customParam4, 
			final String customParam5, 
			final String customParam6, 
			final String customParam7, 
			final String customParam8, 
			final String customParam9, 
			final String customParam10,
			final Policy... policies ) {
		this.mutex = new Object();
		//		this.filename = PathResolver.resolve(filename);
		this.filename = filename;
		this.backups = Math.max(0, backups);
		this.buffered = buffered;
		this.labeler = labeler == null ? new CountLabeler() : labeler;
		this.policies = policies == null || policies.length == 0 ? Arrays.asList(new StartupPolicy()) : Arrays.asList(policies);

		this.customTag1 = customTag1;
		this.customTag2 = customTag2;
		this.customTag3 = customTag3;
		this.customTag4 = customTag4;
		this.customTag5 = customTag5;
		this.customTag6 = customTag6;
		this.customTag7 = customTag7;
		this.customTag8 = customTag8;
		this.customTag9 = customTag9;
		this.customTag10 = customTag10;

		this.customParam1 = customParam1;
		this.customParam2 = customParam2;
		this.customParam3 = customParam3;
		this.customParam4 = customParam4;
		this.customParam5 = customParam5;
		this.customParam6 = customParam6;
		this.customParam7 = customParam7;
		this.customParam8 = customParam8;
		this.customParam9 = customParam9;
		this.customParam10 = customParam10;
	}

	//	@Override
	public Set<LogEntryValue> getRequiredLogEntryValues() {
		return EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY);
	}

	/**
	 * Get the filename of the current log file.
	 *
	 * @return Filename of the current log file
	 */
	public String getFilename() {
		synchronized (mutex) {
			return file == null ? filename : file.getAbsolutePath();
		}
	}

	/**
	 * Determine whether buffered writing is enabled.
	 *
	 * @return <code>true</code> if buffered writing is enabled, otherwise <code>false</code>
	 */
	public boolean isBuffered() {
		return buffered;
	}

	/**
	 * Get the maximum number of backups.
	 *
	 * @return Maximum number of backups
	 */
	public int getNumberOfBackups() {
		return backups;
	}

	/**
	 * Get the labeler for naming backups.
	 *
	 * @return Labeler for naming backups
	 */
	public Labeler getLabeler() {
		return labeler;
	}

	/**
	 * Get the rollover strategies.
	 *
	 * @return Rollover strategies
	 */
	public List<? extends Policy> getPolicies() {
		return Collections.unmodifiableList(policies);
	}

	//	@Override
	public void init(final Configuration configuration) throws IOException {
		File baseFile = new File(filename);
		EnvironmentHelper.makeDirectories(baseFile);

		labeler.init(configuration);
		file = labeler.getLogFile(baseFile);

		for (Policy policy : policies) {
			policy.init(configuration);
		}
		for (Policy policy : policies) {
			if (!policy.check(file)) {
				resetPolicies();
				file = labeler.roll(file, backups);
				break;
			}
		}

		if (buffered) {
			stream = new BufferedOutputStream(new FileOutputStream(file, true), BUFFER_SIZE);
		} else {
			stream = new FileOutputStream(file, true);
		}

		VMShutdownHook.register(this);
	}

	//	@Override
	public void write(final LogEntry logEntry) throws IOException {
		
		l  = logEntry.getRenderedLogEntry();

		if( customTag1   != null) l = l.replace(customTag1,   ( customParam1   == null  ? "" : customParam1) );
		if( customTag2   != null) l = l.replace(customTag2,   ( customParam2   == null  ? "" : customParam2) );
		if( customTag3   != null) l = l.replace(customTag3,   ( customParam3   == null  ? "" : customParam3) );
		if( customTag4   != null) l = l.replace(customTag4,   ( customParam4   == null  ? "" : customParam4) );
		if( customTag5   != null) l = l.replace(customTag5,   ( customParam5   == null  ? "" : customParam5) );
		if( customTag6   != null) l = l.replace(customTag6,   ( customParam6   == null  ? "" : customParam6) );
		if( customTag7   != null) l = l.replace(customTag7,   ( customParam7   == null  ? "" : customParam7) );
		if( customTag8   != null) l = l.replace(customTag8,   ( customParam8   == null  ? "" : customParam8) );
		if( customTag9   != null) l = l.replace(customTag9,   ( customParam9   == null  ? "" : customParam9) );
		if( customTag10  != null) l = l.replace(customTag10,  ( customParam10  == null  ? "" : customParam10) );

		
		
		
		byte[] data = l.getBytes();

		synchronized (mutex) {
			if (!checkPolicies(l)) {
				stream.close();
				file = labeler.roll(file, backups);
				if (buffered) {
					stream = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
				} else {
					stream = new FileOutputStream(file);
				}
			}
			stream.write(data);
		}
	}

	//	@Override
	public void flush() throws IOException {
		if (buffered) {
			synchronized (mutex) {
				stream.flush();
			}
		}
	}

	/**
	 * Close the log file.
	 *
	 * @throws IOException
	 *             Failed to close the log file
	 */
	//	@Override
	public void close() throws IOException {
		synchronized (mutex) {
			VMShutdownHook.unregister(this);
			stream.close();
		}
	}

	private boolean checkPolicies(final String logEntry) {
		for (Policy policy : policies) {
			if (!policy.check(logEntry)) {
				resetPolicies();
				return false;
			}
		}
		return true;
	}

	private void resetPolicies() {
		for (Policy policy : policies) {
			policy.reset();
		}
	}

}