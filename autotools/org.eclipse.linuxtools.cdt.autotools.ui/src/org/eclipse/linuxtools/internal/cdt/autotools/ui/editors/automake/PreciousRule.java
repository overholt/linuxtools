/*******************************************************************************
 * Copyright (c) 2000, 2006 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.internal.cdt.autotools.ui.editors.automake;

/**
 * .PRECIOUS
 * Prerequisites of this special target shall not be removed if make recieves an
 * asynchronous events.
 */
public class PreciousRule extends SpecialRule implements IPreciousRule {

	public PreciousRule(Directive parent, String[] reqs) {
		super(parent, new Target(MakeFileConstants.RULE_PRECIOUS), reqs, new Command[0]);
	}

}
