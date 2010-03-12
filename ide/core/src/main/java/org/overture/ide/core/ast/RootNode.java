package org.overture.ide.core.ast;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;

public class RootNode<T> implements IVdmElement<T>
{
	private boolean checked = false;
	private Hashtable<String, Boolean> parseCurrectTable = new Hashtable<String, Boolean>();

	private Date checkedTime;
	private List<T> rootElementList;

	public RootNode(List<T> modules) {
		this.rootElementList = modules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#setRootElementList(java.util.List)
	 */
	public synchronized void setRootElementList(List<T> rootElementList)
	{
		this.rootElementList = rootElementList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#getRootElementList()
	 */
	public synchronized List<T> getRootElementList()
	{
		return rootElementList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#getCheckedTime()
	 */
	public synchronized Date getCheckedTime()
	{
		return checkedTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#setChecked(boolean)
	 */
	public synchronized void setChecked(boolean checked)
	{
		this.checked = checked;
		this.checkedTime = new Date();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#isChecked()
	 */
	public synchronized boolean isChecked()
	{
		return checked;
	}

	/***
	 * Updates the local definition list with a new list of Definitions if any definition exists the old
	 * definitions are replaced
	 * 
	 * @param module
	 *            the new definition
	 */
	// @SuppressWarnings("unchecked")
	// public synchronized void update(List<T> modules)
	// {
	// this.setChecked(false);
	// if (this.rootElementList.size() != 0)
	// for (Object module : modules)
	// {
	// if (module instanceof ClassDefinition)
	// update((ClassDefinition) module);
	// else if (module instanceof Module)
	// update((Module) module);
	// }
	// else
	// {
	// this.rootElementList.addAll(modules);
	// }
	//
	// }

	// /***
	// * Updates the local list with a new Definition if it already exists the
	// old
	// * one is replaced
	// *
	// * @param module
	// * the new definition
	// */
	// @SuppressWarnings("unchecked")
	// private void update(Module module)
	// {
	// Module existingModule = null;
	// for (Object m : this.rootElementList)
	// {
	// if (m instanceof Module
	// && ((Module) m).name.equals(module.name)
	// && ((Module) m).name.location.file.getName()
	// .equals(module.name.location.file.getName()))
	// existingModule = (Module) m;
	// }
	//
	// if (existingModule != null)
	// this.rootElementList.remove(existingModule);
	//
	// this.rootElementList.add(module);
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#update(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public synchronized void update(List<T> modules)
	{

		this.setChecked(false);
		if (this.rootElementList.size() != 0)
			for (Object module : modules)
			{

				// if (module instanceof ClassDefinition)
				// update((ClassDefinition) module);
				// else if (module instanceof Module)
				// update((Module) module);

				T existingModule = null;
				if (module instanceof ClassDefinition)
				{

					for (Object m : this.rootElementList)
					{
						if (m instanceof ClassDefinition
								&& ((ClassDefinition) m).name.equals(((ClassDefinition) module).name))
							existingModule = (T) m;
					}
				} else if (module instanceof Module)
				{
					for (Object m : this.rootElementList)
					{
						if (m instanceof Module
								&& ((Module) m).name.equals(((Module) module).name)
								&& ((Module) m).name.location.file.getName()
										.equals(((Module) module).name.location.file.getName()))
							existingModule = (T) m;
					}
				}
				if (existingModule != null)
					this.rootElementList.remove(existingModule);

				this.rootElementList.add((T) module);
			}
		else
		{
			this.rootElementList.addAll(modules);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#hasFile(java.io.File)
	 */
	public synchronized boolean hasFile(File file)
	{
		for (Object o : rootElementList)
		{
			if (o instanceof Module
					&& ((Module) o).name.location.file.equals(file))
				return true;
			else if (o instanceof ClassDefinition
					&& ((ClassDefinition) o).name.location.file.equals(file))
				return true;

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#getModuleList()
	 */
	public synchronized ModuleList getModuleList() throws NotAllowedException
	{
		ModuleList modules = new ModuleList();
		for (Object definition : rootElementList)
		{
			if (definition instanceof Module)
				modules.add((Module) definition);
			else
				throw new NotAllowedException("Other definition than Module is found: "
						+ definition.getClass().getName());
		}
		return modules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#getClassList()
	 */
	public synchronized ClassList getClassList() throws NotAllowedException
	{
		ClassList classes = new ClassList();
		for (Object definition : rootElementList)
		{
			if (definition instanceof ClassDefinition)
				classes.add((ClassDefinition) definition);
			else
				throw new NotAllowedException("Other definition than ClassDefinition is found: "
						+ definition.getClass().getName());
		}
		return classes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#hasClassList()
	 */
	public synchronized boolean hasClassList()
	{
		for (Object definition : rootElementList)
		{
			if (definition instanceof ClassDefinition)
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#hasModuleList()
	 */
	public synchronized boolean hasModuleList()
	{
		for (Object definition : rootElementList)
		{
			if (definition instanceof Module)
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#setParseCorrect(java.lang.String, java.lang.Boolean)
	 */
	public synchronized void setParseCorrect(String file, Boolean isParseCorrect)
	{
		if (parseCurrectTable.containsKey(file))
			parseCurrectTable.remove(file);

		parseCurrectTable.put(file, isParseCorrect);
		checked = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.core.ast.IVdmElement#isParseCorrect()
	 */
	public synchronized boolean isParseCorrect()
	{
		for (Boolean isCurrect : parseCurrectTable.values())
			if (!isCurrect)
				return false;
		return true;
	}

	@Override
	public boolean exists()
	{
		return rootElementList != null && rootElementList.size() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IVdmElement filter(IFile file)
	{
		List modules = new Vector();

		for (Object o : rootElementList)
		{
			if (o instanceof Module
					&& ((Module) o).name.location.file.equals(file))
				modules.add(o);
			else if (o instanceof ClassDefinition
					&& ((ClassDefinition) o).name.location.file.equals(file))
				modules.add(o);

		}
		if (modules.size() > 0)
			return new RootNode<T>(modules);
		else
			return null;

	}
}
