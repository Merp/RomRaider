package com.romraider.definition;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.apache.log4j.Logger.getLogger;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.ResetCommand.ResetType;
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

import com.romraider.Settings;
import com.romraider.swing.AbstractFrame;
import com.romraider.util.SettingsManager;

public final class DefinitionRepoManager extends AbstractFrame{

	private static final long serialVersionUID = -8376649924531989081L;
	private static final Logger LOGGER = getLogger(DefinitionRepoManager.class);
	private Settings settings = SettingsManager.getSettings();
    private static Repository gitRepo;
    private static Git git;
    private JWindow startStatus;
    private final JLabel startText = new JLabel("Initializing Defintion Repo");
    private JProgressBar progressBar = startbar();
	
	public DefinitionRepoManager(){
	}
	//TODO: Make sure loading a different repo/branch reloads the definitions!!
	//TODO: Implement hard RESET instead of checkout!
	public void Load(){
		UpdateStatus("Checking Definition Repo Status...",10);
        try{
        	if(!InitAndCheckRepoExists()){
        		if ( JOptionPane.showConfirmDialog( null,"Definition files are missing. Download them from: " + settings.defaultGitUrl + " ?? This may take a few minutes!!",
                        "Download Definitions?", JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE ) == JOptionPane.YES_OPTION )
        		{
	        		LOGGER.info("No definition git repo found, downloading.");
		        	UpdateStatus("Downloading Definition Repo...",50);
		        	DownloadRepo();
		        	UpdateDefRepo();
        		}
        		showMessageDialog(this,
	                    "Definition repository successfully configured! ECU definition file(s) must be selected before ROM images can be opened.\nMenu: ECU Definitions > ECU Definition Manager...",
	                    "Editor Configuration",
	                    INFORMATION_MESSAGE);
        		if ( JOptionPane.showConfirmDialog( null,"Would you like to enable definition auto-updates??",
                        "Definition Configuration?", JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE ) == JOptionPane.YES_OPTION )
        		{
        			settings.setGitAutoUpdate(true);
        		}
        		else
        			settings.setGitAutoUpdate(false);
	        }
	        else{
	        	if(settings.getGitAutoUpdate()) {
		        	LOGGER.info("Definition git repo found, updating.");
		        	UpdateStatus("Updating Definition Repo...",75);
			    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			    	UpdateDefRepo();
		        	setCursor(null);
	        	}
	        }
	        this.startStatus.dispose();		
        }catch(Exception e){
        	LOGGER.error("Error configuring definition git repo: " + e.getMessage());
        	showMessageDialog(this,
                    "Error configuring definition git repository, configure definitions manually!\nError: " + e.getMessage(),
                    "Definition repository configuration failed.",
                    INFORMATION_MESSAGE);
        }
	}
	
	public void SelectNewBranch(String branch,String remote){
		settings.setGitBranch(branch);
		settings.setGitRemoteName(remote);
		UpdateDefRepo();
	}
	
	public void UpdateDefRepo() {
		String remote = settings.getGitCurrentRemoteName();
		String branch = settings.getGitBranch();
		CheckoutRemoteBranch(remote, branch);
	}
	
	public void UpdateDefinitions(){
    	settings.UpdateEcuDefs();
    	settings.UpdateLoggerDefs();
    	settings.CheckCarDefs();
    	settings.SetDefaultDefs();
	}

	/**
	 * Checks that we have a git repo containing our desired branch
	 * @return
	 */
	public boolean InitAndCheckRepoExists(){
		try {
			gitRepo = initRepo(settings.getGitDefsBaseDir());
			git = new Git(gitRepo);
			return CheckLocalBranchExists(gitRepo, settings.getGitBranch());
		} catch (IOException e) {
			LOGGER.error("Error initializing definition git repo: " + e.getMessage());
			return false;
		}
	}
	
	public void DownloadRepo(){
		try {
	    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));    	
			GitClone(settings.defaultGitUrl, settings.defaultGitRemote, settings.getGitDefsBaseDir(), settings.getGitBranch());
			setCursor(null);
			showMessageDialog(this,
	                "Definition git repo successfully downloaded!",
	                "Definition Repo Configuration",
	                INFORMATION_MESSAGE);
			
		} catch (IOException e) {
			LOGGER.error("Error downloading definition git repo: " + e.getMessage());
		}
	}
	
	private void CheckoutRemoteBranch(String remote, String branch) {
		try {
			git.fetch()
				.setRemote(remote)
				.setRemoveDeletedRefs(true)
				.call();
			UpdateAllBranches(settings.getGitRemotes().get(remote), branch);
		} catch (HeadlessException e){			
			LOGGER.error("Error updating definition git repo: " + e.getMessage());
		} catch (GitAPIException e) {
			LOGGER.error("Error updating definition git repo: " + e.getMessage());
		}
	}

	public void GitClone(String url, String remote, String path, String checkoutBranch) throws IOException{
		try {
			LOGGER.info("Cloning git repo " + remote + " at " + url);
			delDir(new File(path));
			git = Git.cloneRepository()
					.setRemote(remote)
					.setURI(url)
					.setDirectory(new File(path + "/"))
					.setCloneAllBranches(true)
					.setTimeout(10000)
					.call();
			FetchAll();
			//CheckoutBranch(checkoutBranch);
			
		} catch (GitAPIException e) {
			LOGGER.error("Error cloning git repo: " + e.getMessage());
		}
	}
	
	private void FetchAll()
	{
		LOGGER.info("Fetching all git remotes");
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
					LOGGER.debug("Fetching git remote " + rem);
					tl.add(rem);
					git.fetch().setRemote(rem);
				}
			}
		} catch (GitAPIException e) {
			LOGGER.error("Error fetching git remotes: " + e.getMessage());
		}
	}
	
	private void UpdateAllBranches(String url, String checkoutBranch) throws GitAPIException
	{
		LOGGER.info("Updating all git branches from remote at " + url);
		List<Ref> bl = git.branchList().setListMode(ListMode.REMOTE).call();
		
		for(Ref r : bl)
		{
			try{
				UpdateBranch(r);
			} catch (GitAPIException e) {
				LOGGER.error("Error updating definition git repo branches: " + e.getMessage());
			}
		}
		CheckoutBranch(checkoutBranch);
		LOGGER.info("Successfully updated definition git repo");
	}
	
	private void UpdateBranch(Ref r) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException
	{
		LOGGER.info("Updating git branch " + r);
		String sbranch = Repository.shortenRefName(r.getName());
		git.branchCreate()
		.setForce(true)
		.setName(sbranch)
        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
        .setStartPoint(sbranch)
        .call();
		//settings.setGitBranch(sbranch);
	}

	public void CheckoutBranch(String s) {
		LOGGER.info("Checking out git branch " + s);
		git = new Git(gitRepo);
		try {
			git.checkout().setName(s).setUpstreamMode(SetupUpstreamMode.TRACK).call();
			ResetHardBranch(s);
		} catch (GitAPIException e) {
			LOGGER.error("Error checking out definition git repo branch " + s + ": " + e.getMessage());
		}
        UpdateDefinitions();
	}
	
	public void ResetHardBranch(String s){
		LOGGER.info("Resetting to git branch " + s);
		git = new Git(gitRepo);
		try {
			git.reset().setMode(ResetType.HARD).setRef(s).call();
			settings.setGitBranch(s);
			settings.UpdateDefs();
		} catch (GitAPIException e) {
			LOGGER.error("Error resetting to git repo branch " + s + ": " + e.getMessage());
		}
	}
	public static void gitClone(String url, String path, List<String> branches) throws IOException, InvalidRemoteException, TransportException, GitAPIException{
		LOGGER.info("Cloning git repository at " + url + " to path " + path );
		Git.cloneRepository()
			.setURI(url)
			.setDirectory(new File(path + "/"))
			.setBranchesToClone(branches)
			.setTimeout(10000)
			.call();
		gitRepo = initRepo(path);
	}

	public static Repository initRepo(String path) throws IOException{
		LOGGER.info("Initializing repository at path " + path);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = null;
		repository = builder.setGitDir(new File(path + "/.git"))
		  .readEnvironment()
		  .findGitDir() 
		  .build();
		return repository;
	}
	
	public boolean CheckLocalBranchExists(Repository repo, String branch) throws IOException{
		Ref r;
		r = repo.getRef(branch);
		if(r == null)
			return false;
		else
			return true;
	}
	
	public static boolean delDir(File directory) {
		LOGGER.debug("Deleting directory " + directory.getAbsolutePath());
		try{
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
		}catch(Exception e){
			LOGGER.error("error deleting directory " + directory + ": " + e.getMessage());
			return false;
		}
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
			LOGGER.error("Error getting definition git repo available local branches: " + e.getMessage());
		}
		return tv;
	}
	
	public Vector<String> getAvailableBranches() {
		Vector<String> tv = new Vector<String>();
		try {
			List<Ref> trl = git.branchList().setListMode(ListMode.REMOTE).call();
			for(Ref r : trl)
			{
				tv.add(r.getName()); //Repository.shortenRefName(r.getName()));
			}
		} catch (GitAPIException e) {
			LOGGER.error("Error getting definition git repo available branches" + e.getMessage());
		}
		return tv;
	}

	public String getCurrentBranch() {
		try {
			return gitRepo.getFullBranch();
		} catch (IOException e) {
			LOGGER.error("Error getting definition git repo current branch: " + e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("static-access")
	public void AddRemote(String name, String url) {
		LOGGER.debug("Adding git remote " + name + " with url " + url);
		StoredConfig config = git.getRepository().getConfig();
		config.setString("remote", name, "url",url);
		config.setString("remote",name, "fetch", "+refs/heads/*:refs/remotes/" + name + "/*");
		try {
			config.save();
			DefinitionRepoManager.initRepo(settings.getGitDefsBaseDir());
			git.init().call();
			git.fetch().setRemote(name).call();
			settings.addGitRemote(url, name);
		} catch (IOException e){
			LOGGER.error("Error adding definition git repo remote " + name + " with URL " + url + " Error: " + e.getMessage());
		} catch (GitAPIException e) {
			LOGGER.error("Error adding definition git repo remote " + name + " with URL " + url + " Error: " + e.getMessage());
		}		
	}

	public String getGitCurrentHash() {
		return Integer.toHexString(gitRepo.hashCode());
	}
}
