package com.szeba.wyv.utilities;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

public class SpecDirFilter extends AbstractFileFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * Singleton instance of directory filter.
     * @since 1.3
     */
    public static final IOFileFilter DIRECTORY = new SpecDirFilter();
    
    /**
     * Singleton instance of directory filter.
     * Please use the identical DirectoryFileFilter.DIRECTORY constant.
     * The new name is more JDK 1.5 friendly as it doesn't clash with other
     * values when using static imports.
     */
    
    public static final IOFileFilter INSTANCE = DIRECTORY;

    /**
     * Restrictive consructor.
     */
    protected SpecDirFilter() {
    }

    /**
     * Checks to see if the file is a directory.
     *
     * @param file the File to check
     * @return true if the file is a directory
     */
    @Override
    public boolean accept(final File file) {
    	if (FileUtilities.isValidWyvernSpecial(file.getName())) {
    		return false;
    	} else {
    		return file.isDirectory();
    	}
    }

}