/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kent Sebastian <ksebasti@redhat.com> - initial API and implementation
 *      - Note: the original SamplesProcessor class was removed, this is a new 
 *        implementation 
 *******************************************************************************/ 
package org.eclipse.linuxtools.oprofile.core.opxml;

import java.util.ArrayList;

import org.eclipse.linuxtools.oprofile.core.model.OpModelSample;


/**
 * XML handler class for <sample> tags (individual samples).
 */
public class SamplesProcessor extends XMLProcessor {
	//XML tags parsed by this processor
	private static final String SAMPLE_TAG = "sample";
	private static final String COUNT_TAG = "count";
	private static final String LINE_TAG = "line"; 
	private static final String SYMBOL_TAG = "symbol"; 
	
	//the current sample being constructed
	private OpModelSample _sample;
	//a list of all samples (for this symbol)
	private ArrayList<OpModelSample> _sampleList;

	public void reset(Object callData) {
		_sample = new OpModelSample();
		_sampleList = new ArrayList<OpModelSample>();
	}

	/**
	 * @see org.eclipse.linuxtools.oprofile.core.XMLProcessor#endElement(String)
	 */
	public void endElement(String name, Object callData) {
		if (name.equals(COUNT_TAG)) {
			_sample._setCount(Integer.parseInt(_characters));
		} else if (name.equals(LINE_TAG)) {
			_sample._setLine(Integer.parseInt(_characters));
		} else if (name.equals(SAMPLE_TAG)) {
			_sampleList.add(_sample);
			_sample = new OpModelSample();
		} else if (name.equals(SYMBOL_TAG)) {
			OprofileSAXHandler.getInstance(callData).pop(SYMBOL_TAG);
		} else {
			super.endElement(name, callData);
		}
	}
	
	public OpModelSample[] getSamples() {
		OpModelSample[] samples = new OpModelSample[_sampleList.size()];
		_sampleList.toArray(samples);
		return samples;
	}
}