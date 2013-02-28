package com.romraider.definition;

import static com.romraider.Version.ECU_DEFS_URL;
import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
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
import org.eclipse.jgit.util.FileUtils;

import com.centerkey.utils.BareBonesBrowserLaunch;
import com.romraider.ECUExec;
import com.romraider.Settings;
import com.romraider.editor.ecu.ECUEditor;
import com.romraider.logger.ecu.EcuLogger;
import com.romraider.swing.AbstractFrame;

public final class DefinitionRepoManager extends AbstractFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8376649924531989081L;
	private Settings settings = ECUExec.settings;
    private Repository gitRepo;
    private Git git;
    private JWindow startStatus;
    private final JLabel startText = new JLabel(" Initializing Defintion Repo");
    private JProgressBar progressBar = startbar();
	
	public DefinitionRepoManager(){
	}
	
	public void Load(){
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		UpdateStatus("Checking Definition Repo Status...",10);
    	
        try{
        	if(!InitAndCheckRepoExists())
	        {
	        	UpdateStatus("Downloading Definition Repo...",50);
	        	DownloadRepo();
	        }
	        else
	        {
	        	UpdateStatus("Updating Definition Repo...",75);
	        	UpdateDefRepo();
	        }
	        setCursor(null);
	        this.startStatus.dispose();        
        }catch(Exception e){
        	e.printStackTrace();
        	showMessageDialog(this,
                    "Error configuring definition repository, configure definitions manually!\nError: " + e.getMessage(),
                    "Definition repository configuration failed.",
                    INFORMATION_MESSAGE);
        	if (settings.getEcuDefinitionFiles().size() <= 0) {
                // no ECU definitions configured - let user choose to get latest or configure later
                Object[] options = {"Yes", "No"};
                int answer = showOptionDialog(null,
                        "Unable to configure ECU definition repository.\nGo online to download the latest definition files?",
                        "Editor Configuration",
                        DEFAULT_OPTION,
                        WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (answer == 0) {
                    BareBonesBrowserLaunch.openURL(ECU_DEFS_URL);
                } else {
                    showMessageDialog(this,
                            "ECU definition files need to be configured before ROM images can be opened.\nMenu: ECU Definitions > ECU Definition Manager...",
                            "Editor Configuration",
                            INFORMATION_MESSAGE);
                }
    	        
            }
        }
	}

	
//	public void Run(){
//
//    	parentEditor.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//    	parentEditor.statusPanel.update("Checking Definition Repo Status...",10);
//    	
//        try{
//        	if(!InitAndCheckRepoExists())
//	        {
//	        	parentEditor.statusPanel.update("Downloading Definition Repo...",50);
//	        	DownloadRepo();
//	        }
//	        else
//	        {
//	        	parentEditor.statusPanel.update("Updating Definition Repo...",75);
//	        	UpdateDefRepo();
//	        }
//        	showMessageDialog(parentEditor,
//                    "Definition repository successfully configured! ECU definition file(s) must be selected before ROM images can be opened.\nMenu: ECU Definitions > ECU Definition Manager...",
//                    "Editor Configuration",
//                    INFORMATION_MESSAGE);
//        	
//	        parentEditor.statusPanel.update("Ready...",0);
//	        parentEditor.setCursor(null);
//	       
//        }catch(Exception e){
//        	showMessageDialog(parentEditor,
//                    "Error configuring definition repository, configure definitions manually!\nError: " + e.getMessage(),
//                    "Definition repository configuration failed.",
//                    INFORMATION_MESSAGE);
//        	if (parentEditor.getSettings().getEcuDefinitionFiles().size() <= 0) {
//                // no ECU definitions configured - let user choose to get latest or configure later
//                Object[] options = {"Yes", "No"};
//                int answer = showOptionDialog(null,
//                        "Unable to configure ECU definition repository.\nGo online to download the latest definition files?",
//                        "Editor Configuration",
//                        DEFAULT_OPTION,
//                        WARNING_MESSAGE,
//                        null,
//                        options,
//                        options[0]);
//                if (answer == 0) {
//                    BareBonesBrowserLaunch.openURL(ECU_DEFS_URL);
//                } else {
//                    showMessageDialog(parentEditor,
//                            "ECU definition files need to be configured before ROM images can be opened.\nMenu: ECU Definitions > ECU Definition Manager...",
//                            "Editor Configuration",
//                            INFORMATION_MESSAGE);
//                }
//    	        
//            }
//        }
//	}
//	
	public boolean InitAndCheckRepoExists(){
		gitRepo = gitInit(Settings.gitDefsBaseDir);
		Ref hurr;
		try {
			hurr = gitRepo.getRef(settings.gitDefsBranch);
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
				showMessageDialog(this,
	                "Definition files are missing, downloading most up to date set from: " + settings.getGitDefsUrl() + " This may take a few minutes!!",
	                "Definition Configuration",
	                INFORMATION_MESSAGE);
				try {
					
					gitClone(settings.getGitDefsUrl(), settings.gitDefsBaseDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//TODO make this work: Settings.getGitDefsBranch());
				
				showMessageDialog(this,
	                    "Definitions successfully updated!",
	                    "Definition Configuration",
	                    INFORMATION_MESSAGE);
	}
	
	public void UpdateDefRepo() {
		try {
			if(!gitCompare(settings.getGitDefsUrl(), settings.gitDefsBranch, gitRepo)){

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
			    	File tempFile = new File(settings.gitDefsBaseDir);
			    	delete(tempFile);

			    	gitClone(settings.getGitDefsUrl(), settings.gitDefsBaseDir);            	
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
			
			delDir(new File(path));
			
			git = Git.cloneRepository()
					.setURI(url)
					.setDirectory(new File(path + "/"))
					.setCloneAllBranches(true)
					//.setTimeout(10000)
					.call();
			
			List<Ref> bl = git.branchList().setListMode(ListMode.REMOTE).call();
			
			for(Ref r : bl)
			{
				String rbranch = r.getName().replace("refs/heads/", "").replace("refs/remotes/origin/", "");
				String originbranch = r.getName().replace("/heads/", "/remotes/").replace("refs/remotes/origin/", "origin/");
				settings.addGitAvailableBranch(rbranch);
				try{
					String rs = r.getName();
					git.branchCreate()
					//.setForce(true)
					.setName(rbranch)
			        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
			        .setStartPoint("origin/" + rbranch)
			        .call();
//					git.fetch()
//					.setRemote("origin")
//					.setRefSpecs(new RefSpec(r.getTarget().getName()))
//					.call();
				} catch (GitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		//gitInit(path);
		try {
			gitCheckout(settings.gitDefsBranch);
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean delDir(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    delDir(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	
	private void gitCheckout(String s) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Git tempGit = new Git(gitRepo);
		git = new Git(gitRepo);
		List<Ref> rl = git.branchList().setListMode(ListMode.ALL).call();
		git.checkout().setName(s).call();
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
	
	public void UpdateStatus(String s, int i){
		progressBar.setValue(i);
        startText.setText(s);
	}
	
	private JProgressBar startbar() {
        startStatus = new JWindow();
        startStatus.setAlwaysOnTop(true);
        startStatus.setLocation(
                (int)(settings.getLoggerWindowSize().getWidth()/2 + settings.getLoggerWindowLocation().getX() - 150),
                (int)(settings.getLoggerWindowSize().getHeight()/2 + settings.getLoggerWindowLocation().getY() - 36));
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setIndeterminate(false);
        progressBar.setOpaque(true);
        startText.setOpaque(true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(startText, BorderLayout.SOUTH);
        startStatus.getContentPane().add(panel);
        startStatus.pack();
        startStatus.setVisible(true);
        return progressBar;
    }

}
