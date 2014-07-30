package com.mhemmings.lsstorage;

/**
 * An LSObject is what you store in LSStorage. Extend this class however you wish
 * with variables, getters, setters etc. Ultimately, LSObjects are fairly
 * stupid. The mappings between these and tables are done in {@link LSTable}
 * 
 */
public abstract class LSObject {

	/**
	 * Added by default. Needed by some Android adapters that make use of
	 * cursors
	 */
	public final static String _ID = "_id";

}
