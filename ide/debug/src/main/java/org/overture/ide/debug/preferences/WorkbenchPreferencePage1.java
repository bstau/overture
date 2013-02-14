/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.debug.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.overture.ide.debug.core.IDebugPreferenceConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;


public class WorkbenchPreferencePage1 extends
		org.eclipse.jface.preference.FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	@Override
	protected void createFieldEditors()
	{
		IntegerFieldEditor portField = new IntegerFieldEditor(IDebugPreferenceConstants.PREF_DBGP_PORT, "Debug port", getFieldEditorParent());
		portField.setValidRange(-1, Integer.MAX_VALUE);
		addField(portField);
		
		IntegerFieldEditor commTimeoutField = new IntegerFieldEditor(IDebugPreferenceConstants.PREF_DBGP_CONNECTION_TIMEOUT, "Connection timeout", getFieldEditorParent());
		commTimeoutField.setValidRange(0, Integer.MAX_VALUE);
		addField(commTimeoutField);
		
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return VdmDebugPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void performDefaults()
	{
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(IDebugPreferenceConstants.PREF_DBGP_PORT, IDebugPreferenceConstants.DBGP_DEFAULT_PORT);
		super.performDefaults();
	}

	public void init(IWorkbench workbench)
	{

	}

}
