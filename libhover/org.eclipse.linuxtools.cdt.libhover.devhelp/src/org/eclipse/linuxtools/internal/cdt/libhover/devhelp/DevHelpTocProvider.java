/*******************************************************************************
 * Copyright (c) 2011 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat Inc. - Initial implementation
 *******************************************************************************/
package org.eclipse.linuxtools.internal.cdt.libhover.devhelp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.help.AbstractTocProvider;
import org.eclipse.help.IToc;
import org.eclipse.help.ITocContribution;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.linuxtools.cdt.libhover.devhelp.DevHelpPlugin;
import org.eclipse.linuxtools.internal.cdt.libhover.devhelp.preferences.PreferenceConstants;

public class DevHelpTocProvider extends AbstractTocProvider {

	public DevHelpTocProvider() {
	}
	
	private class DevhelpTopic implements ITopic {

		private String name;
		
		DevhelpTopic(String name) {
			this.name = name;
		}
		
		@Override
		public boolean isEnabled(IEvaluationContext context) {
			return true;
		}

		@Override
		public IUAElement[] getChildren() {
			return new IUAElement[0];
		}

		@Override
		public String getHref() {
			return "/" + DevHelpPlugin.PLUGIN_ID + "/" + name + "/index.html"; // $NON-NLS-1$ //$NON-NLS-2$" //$NON-NLS-3$
					
		}

		@Override
		public String getLabel() {
			return name;
		}

		@Override
		public ITopic[] getSubtopics() {
			return null;
		}
	}
	
	@Override
	public ITocContribution[] getTocContributions(String locale) {
		// TODO Auto-generated method stub
        ITocContribution contribution = new ITocContribution() {
            public String getId() {
               // a way to identify our book
               return "org.eclipse.linuxtools.cdt.libhover.devhelp.toc"; //$NON-NLS-1$
            }
            public String getCategoryId() {
               // our book does not belong to any category of books
               return null;
            }
            public boolean isPrimary() {
               // this is a primary, top-level contribution (a book)
               return true;
            }
            public IToc getToc() {
            	return new IToc() {
            		public String getLabel() {
            			return "Devhelp Documents"; //$NON-NLS-1$
            		}
            		public String getHref() {
            			return null;
            		}
            		@Override
            		public boolean isEnabled(IEvaluationContext context) {
            			// TODO Auto-generated method stub
            			return true;
            		}
            		@Override
            		public IUAElement[] getChildren() {
            			// TODO Auto-generated method stub
            			return getTopics();
            		}
            		@Override
            		public ITopic[] getTopics() {
            			try {
            				ArrayList<ITopic> topics = new ArrayList<ITopic>();
            				IPreferenceStore ps = DevHelpPlugin.getDefault().getPreferenceStore();
            				IPath devhelpLocation = new Path(ps.getString(PreferenceConstants.DEVHELP_DIRECTORY));
            				IFileSystem fs = EFS.getLocalFileSystem();
            				IFileStore htmlDir = fs.getStore(devhelpLocation);
            				IFileStore[] files = htmlDir.childStores(EFS.NONE, null);
            				Arrays.sort(files, new Comparator<IFileStore>() {

            					@Override
            					public int compare(IFileStore arg0, IFileStore arg1) {
            						return (arg0.getName().compareToIgnoreCase(arg1.getName()));
            					}
            					
            				});
            				for (int i = 0; i < files.length; ++i) {
            					IFileStore file = files[i];
            					String name = file.fetchInfo().getName();
            					ITopic topic = new DevhelpTopic(name);
            					topics.add(topic);
            				}
            				ITopic[] retval = new ITopic[topics.size()];
            				return topics.toArray(retval);
            			} catch (CoreException e) {
             			}
            			return null;
            		}
            		@Override
            		public ITopic getTopic(String href) {
            			// TODO Auto-generated method stub
            			return null;
            		}
            	};
            }
            public String getLocale() {
            	// this provider only provides content for the en_US locale
            	return "en_US"; //$NON-NLS-1$
            }
            public String[] getExtraDocuments() {
            	// there are no extra documents associated with this book
            	return new String[0];
            }
            public String getLinkTo() {
            	return "../org.eclipse.linuxtools.cdt.libhover.library.docs/libraries.xml#devhelpdocs";
            }
            @Override
            public String getContributorId() {
            	// TODO Auto-generated method stub
            	return "org.eclipse.linuxtools.cdt.libhover.devhelp"; //$NON-NLS-1$
            }
        };
        return new ITocContribution[] { contribution };
	}

}
