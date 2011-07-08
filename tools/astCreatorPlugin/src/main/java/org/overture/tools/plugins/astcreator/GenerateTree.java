package org.overture.tools.plugins.astcreator;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.Main;

/**
 * Generate Tree
 * 
 * @goal astcreate
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public class GenerateTree extends AstCreatorBaseMojo
{

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		getLog().info("Preparing tree generating...");

		if (folderToDeletePreGenerate != null)
		{
			getLog().info("Deleteting folder: " + folderToDeletePreGenerate);
			deleteDir(new File(getProjectJavaSrcDirectory(),folderToDeletePreGenerate.replace('/', File.separatorChar)));
		}

		List<File> treeNames = new Vector<File>();

		for (String name : asts)
		{
			treeNames.add(new File(getResourcesDir(), name));
		}

		for (File file : treeNames)
		{
			if (file.exists())
			{
				File generated = getProjectJavaSrcDirectory();
				getLog().info("Generator starting with input: " + file);
				String defaultPackage = "org.overture.ast.node";
				String analysisPackage = "org.overture.ast.analysis";
				Environment env1 = null;
				try
				{
					env1 = Main.create(file.getAbsolutePath(), defaultPackage, analysisPackage, "", generated, true);
				} catch (Exception e)
				{
					getLog().error(e);
				}
				if (env1 != null)
				{
					getLog().info("Generator completed with "
							+ env1.getAllDefinitions().size()
							+ " generated files.\n\n");
				}
			}
		}

	}

	public static boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

}
