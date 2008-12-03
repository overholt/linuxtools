/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kent Sebastian <ksebasti@redhat.com> - initial API and implementation 
 *******************************************************************************/ 
package org.eclipse.linuxtools.oprofile.ui.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.linuxtools.oprofile.core.model.OpModelSample;
import org.eclipse.linuxtools.oprofile.ui.OprofileUiPlugin;
import org.eclipse.swt.graphics.Image;

public class UiModelSample implements IUiModelElement {
	private IUiModelElement _parent;		//parent element
	private OpModelSample _sample;			//the node in the data model
	private int _totalCount;				//total sample count for the parent session
	
	public UiModelSample(IUiModelElement parent, OpModelSample sample, int totalCount) {
		_parent = parent;
		_sample = sample;
		_totalCount = totalCount;
	}
	
	@Override
	public String toString() {
		NumberFormat nf = NumberFormat.getPercentInstance();
		if (nf instanceof DecimalFormat) {
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);
		}
		double countPercentage = (double)_sample.getCount() / (double)_totalCount;
		
		String percentage;
		if (countPercentage < 0.0001) {
			percentage = "<" + nf.format(0.0001);
		} else {
			percentage = nf.format(countPercentage);
		}
		
		return percentage + " on line " + Integer.toString(_sample.getLine());
	}
	
	public int getLine() {
		return _sample.getLine();
	}
	
	/** IUiModelElement functions **/
	public String getLabelText() {
		return toString();
	}

	public IUiModelElement[] getChildren() {
		return null;
	}

	public boolean hasChildren() {
		return false;		//bottom level element
	}

	public IUiModelElement getParent() {
		return _parent;
	}

	public Image getLabelImage() {
		return OprofileUiPlugin.getImageDescriptor(OprofileUiPlugin.SAMPLE_ICON).createImage();
	}
}
