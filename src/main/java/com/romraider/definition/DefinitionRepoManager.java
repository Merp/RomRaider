package com.romraider.definition;

import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;

import com.romraider.Settings;
import com.romraider.swing.AbstractFrame;

public final class DefinitionRepoManager {

	private AbstractFrame parent;
    private Repository gitRepo;
	
	public DefinitionRepoManager(AbstractFrame p){
		parent = p;
	}
	
	public boolean InitAndCheckRepoExists(){
	
		gitRepo = gitInit(Settings.gitDefsBaseDir);
		Ref hurr;
		try {
			hurr = gitRepo.getRef(Settings.gitDefsBranch);
			if(hurr == null)
				return false;
			else
				return true;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void DownloadRepo(){
				showMessageDialog(parent,
	                "Definition files are missing, downloading most up to date set from: " + Settings.gitDefsUrl + "***###This may take a few minutes!!!###***",
	                "Definition Configuration",
	                INFORMATION_MESSAGE);
				try {
					gitClone(Settings.gitDefsUrl, Settings.gitDefsBaseDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//TODO make this work: Settings.getGitDefsBranch());
				
				showMessageDialog(parent,
	                    "Definitions successfully updated!",
	                    "Definition Configuration",
	                    INFORMATION_MESSAGE);
	}
	
	public void UpdateDefRepo() {
		try {
			if(!gitCompare(Settings.gitDefsUrl, Settings.gitDefsBranch, gitRepo)){

			    Object[] options = {"Do it. Do it.","Maybe Later"};
			    int answer = showOptionDialog(null,
			            "ECU definition repository is out of date\n Would you like to update??",
			            "Editor Configuration",
			            DEFAULT_OPTION,
			            WARNING_MESSAGE,
			            null,
			            options,
			            options[0]);
			    if (answer == 0) {
			    	//These lines will pull-merge. Not useful here, but maybe somewhere else.
			    	//Git tempGit = new Git(gitRepo);
			    	//tempGit.pull().call();
			    	File tempFile = new File(Settings.gitDefsBaseDir);
			    	delete(tempFile);

			    	gitClone(Settings.gitDefsUrl, Settings.gitDefsBaseDir);            	
			    }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Repository gitInit(String path){
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = null;
		try {
			repository = builder.setGitDir(new File(path + "/.git"))
			  .readEnvironment() // scan environment GIT_* variables
			  .findGitDir() // scan up the file system tree
			  .build();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return repository;
		
	}

	public void gitClone(String url, String path) throws IOException{
		try {
			Git.cloneRepository()
				.setURI(url)
				.setDirectory(new File(path + "/"))
				.setCloneAllBranches(true)
				.setTimeout(10000)
				.call();
		} catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Git tg = new Git(gitRepo);
		try {
			for (String branch : Settings.gitDefsBranches)
			{
				tg.fetch().setRemote("origin")
				.setRefSpecs(new RefSpec("refs/heads/" + branch + ":refs/heads/" + branch))
				.call();
			}
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		gitInit(path);
		try {
			gitCheckout(Settings.gitDefsBranch);
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void gitCheckout(String gitDefsBranch) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		Git tempGit = new Git(gitRepo);
		tempGit.checkout().setName(Settings.gitDefsBranch).call();
	}
	
	public static void gitClone(String url, String path, List<String> branches) throws IOException{
			try {
				Git.cloneRepository()
					.setURI(url)
					.setDirectory(new File(path + "/"))
					.setBranchesToClone(branches)
					.setTimeout(10000)
					.call();
			} catch (InvalidRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		gitInit(path);
	}
	
	public static boolean gitCompare(String url, String branch, Repository repo) throws IOException{
			LsRemoteCommand lsrc = new LsRemoteCommand(repo);
			lsrc.setHeads(true);
			lsrc.setTags(true);
			lsrc.setRemote(url);
			try {
				Collection<Ref> derp = lsrc.call();
				for(Ref ref : derp){
					if(ref.getName().contains(branch)){
						Ref reff = repo.getRef(branch);
						if(reff.getObjectId().equals(ref.getObjectId())) {
						return true;
						}
					}
					
				}
			} catch (InvalidRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
	}
	public static void delete(File td){
		if(td.isDirectory()){
	    	//directory is empty, then delete it
			if(td.list().length==0){
	
			   td.delete();
			   System.out.println("Directory is deleted : " 
	                                             + td.getAbsolutePath());
	
			}else{
	
			   //list all the directory contents
	    	   String files[] = td.list();
	
	    	   for (String temp : files) {
	    	      //construct the file structure
	    	      File fileDelete = new File(td, temp);
	
	    	      //recursive delete
	    	     delete(fileDelete);
	    	   }
	
	    	   //check the directory again, if empty then delete it
	    	   if(td.list().length==0){
	       	     td.delete();
	    	     System.out.println("Directory is deleted : " 
	                                              + td.getAbsolutePath());
	    	   }
			}
		}
		else{
			//if file, then delete it
			td.delete();
			System.out.println("File is deleted : " + td.getAbsolutePath());
		}
	}

}
