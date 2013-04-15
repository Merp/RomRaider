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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import com.centerkey.utils.BareBonesBrowserLaunch;
import com.romraider.ECUExec;
import com.romraider.Settings;
import com.romraider.swing.AbstractFrame;

public final class DefinitionRepoManager extends AbstractFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8376649924531989081L;
	private Settings settings = ECUExec.settings;
    private static Repository gitRepo;
    private static Git git;
    private JWindow startStatus;
    private final JLabel startText = new JLabel(" Initializing Defintion Repo");
    private JProgressBar progressBar = startbar();
	
	public DefinitionRepoManager(){
	}
	
	public void Load(){
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
		    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	        	FetchAll();
	        	CheckoutBranch(settings.getGitBranch());
	        	setCursor(null);
	        }
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

	/**
	 * Checks that we have a git repo containing our desired branch
	 * @return
	 */
	public boolean InitAndCheckRepoExists(){
		try {
			gitRepo = initRepo(Settings.getGitDefsBaseDir());
			git = new Git(gitRepo);
			return CheckLocalBranchExists(gitRepo, settings.getGitBranch());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	}
	
	public void DownloadRepo(){
		showMessageDialog(this,
            "Definition files are missing, downloading most up to date set from: " + Settings.defaultGitUrl + " This may take a few minutes!!",
            "Definition Configuration",
            INFORMATION_MESSAGE);
		try {
			
	    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			gitClone(Settings.defaultGitUrl, Settings.defaultGitRemote, Settings.getGitDefsBaseDir(), settings.getGitBranch());
			setCursor(null);
			
			showMessageDialog(this,
                "Definitions successfully updated!",
                "Definition Configuration",
                INFORMATION_MESSAGE);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void UpdateDefRepo(String remote, String branch) {
		try {
			git.fetch()
				.setRemote(remote)
				.setRemoveDeletedRefs(true)
				.call();
			
			this.UpdateAllBranches(ECUExec.settings.getGitRemotes().get(remote), branch);
		} catch (HeadlessException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void gitClone(String url, String remote, String path, String checkoutBranch) throws IOException{
		try {
			
			delDir(new File(path));
			
			git = Git.cloneRepository()
					.setRemote(remote)
					.setURI(url)
					.setDirectory(new File(path + "/"))
					.setCloneAllBranches(true)
					.setTimeout(10000)
					.call();
			FetchAll();
			
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void FetchAll()
	{
		List<Ref> bl;
		try {
			bl = git.branchList().setListMode(ListMode.REMOTE).call();
			List<String> tl = new ArrayList<String>();
			for(Ref r : bl)
			{
				String[] bra = r.getName().split("/");
				String rem = bra[bra.length-2];
				if(!tl.contains(rem))
				{
					tl.add(rem);
					git.fetch().setRemote(rem);
				}
			}
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void UpdateAllBranches(String url, String checkoutBranch) throws GitAPIException
	{
		List<Ref> bl = git.branchList().setListMode(ListMode.REMOTE).call();
		
		for(Ref r : bl)
		{
			try{
				UpdateBranch(r);
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CheckoutBranch(settings.getGitBranch());
	}
	
	private void UpdateBranch(Ref r) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException
	{
		String sbranch = Repository.shortenRefName(r.getName());
		git.branchCreate()
		.setForce(true)
		.setName(sbranch)
        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
        .setStartPoint(sbranch)
        .call();
		settings.setGitBranch(sbranch);
	}

	public void CheckoutBranch(String s) {
		git = new Git(gitRepo);
		try {
			git.checkout().setName(s).setUpstreamMode(SetupUpstreamMode.TRACK).call();
			settings.setGitBranch(s);
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void gitClone(String url, String path, List<String> branches) throws IOException, InvalidRemoteException, TransportException, GitAPIException{
		Git.cloneRepository()
			.setURI(url)
			.setDirectory(new File(path + "/"))
			.setBranchesToClone(branches)
			.setTimeout(10000)
			.call();
		gitRepo = initRepo(path);
	}

	public static Repository initRepo(String path) throws IOException{
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = null;
		repository = builder.setGitDir(new File(path + "/.git"))
		  .readEnvironment()
		  .findGitDir() 
		  .build();
		return repository;
	}
	
	public boolean CheckLocalBranchExists(Repository repo, String branch) throws IOException{
		Ref hurr;
		hurr = repo.getRef(branch);
		if(hurr == null)
			return false;
		else
			return true;
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
	
	public void UpdateStatus(String s, int i){
		progressBar.setValue(i);
        startText.setText(s);
	}
	
	private JProgressBar startbar() {
        startStatus = new JWindow();	
        startStatus.setSize(300,50);
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
        progressBar.setVisible(true);
        return progressBar;
    }

	public Vector<String> getAvailableLocalBranches() {
		// TODO Auto-generated method stub
		Vector<String> tv = new Vector<String>();
		try {
			List<Ref> trl = git.branchList().setListMode(ListMode.ALL).call();
			for(Ref r : trl)
			{
				if(!r.getName().contains("remotes"))
				{
					tv.add(r.getName().replace("refs/heads/", ""));
				}
			}
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tv;
	}
	
	public Vector<String> getAvailableBranches() {
		// TODO Auto-generated method stub
		Vector<String> tv = new Vector<String>();
		try {
			List<Ref> trl = git.branchList().setListMode(ListMode.REMOTE).call();
			for(Ref r : trl)
			{
				tv.add(r.getName()); //Repository.shortenRefName(r.getName()));
			}
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tv;
	}

	public String getCurrentBranch() {
		try {
			return gitRepo.getFullBranch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void AddRemote(String name, String url) {
		StoredConfig config = git.getRepository().getConfig();
		config.setString("remote", name, "url",url);
		config.setString("remote",name, "fetch", "+refs/heads/*:refs/remotes/" + name + "/*");
		try {
			config.save();
			DefinitionRepoManager.initRepo(Settings.getGitDefsBaseDir());
			git.init().call();
			git.fetch().setRemote(name).call();
			settings.addGitRemote(url, name);
		} catch (IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
