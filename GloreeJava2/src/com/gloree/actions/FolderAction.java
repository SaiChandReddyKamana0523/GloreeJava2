package com.gloree.actions;


import com.gloree.beans.*;
import com.gloree.utils.FolderUtil;
import com.gloree.utils.SecurityUtil;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to create , delete and edit a folder.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////


public class FolderAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public FolderAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String databaseType = this.getServletContext().getInitParameter("databaseType");

		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// user has to be logged in by the time he is here. 
		// And he needs to be an Member
		// of this project.
		// We are taking a conscious decision to let any user be able to create folders.
		// NOTE :  unless they have delete privs on requirements, in the folder, they won't
		// be able to delete the folder.
		///////////////////////////////SECURITY//////////////////////////////
		
		// see if the user is logged in. If he is not, the method below will
		// redirect him to the log in page.
		if (!(SecurityUtil.authenticationPassed(request, response))){
			return;
		}
		
		// now check if this users should be in this project.
		HttpSession session = request.getSession(true);
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		Project project = (Project) session.getAttribute("project");
		if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
			//User is NOT a member of this project. so do nothing and return.
			return;
		}
		
		User user = securityProfile.getUser();
		///////////////////////////////SECURITY//////////////////////////////
		
		String action = request.getParameter("action");
		
		
		if ( action.equals("createFolder")){
				
			String parentFolderIdString = request.getParameter("parentFolderId");
			int parentFolderId = Integer.parseInt(parentFolderIdString);
			Folder parentFolder = new Folder(parentFolderId);
			
			String projectIdString = request.getParameter("projectId");
			int projectId = Integer.parseInt(projectIdString);
			
			String folderName = request.getParameter("folderName");
			String folderDescription = request.getParameter("folderDescription");
			
			String type = request.getParameter("type");
			
			
			if (type.equals("single")){
				// NOTE : YUI has issues in explorer if the name  has ' or ". so replacing
				// them with ^.
				// Same with folderDescription.
				folderName = folderName.replace('\'', '^');
				folderName = folderName.replace('"', '^');
				folderName = folderName.replace("::", "--");
				
				folderDescription = folderDescription.replace('\'', '^');
				folderDescription = folderDescription.replace('"', '^');
				folderDescription = folderDescription.replace("::", "--");
				folderDescription = folderDescription.replace('\n', ' ');
				folderDescription = folderDescription.replace('\r', ' ');
				
	
	
				// for all sub folders (ie non root level folders), we default the 
				// folderOrder to 0. Since we use the sorting by folder_order, folder_name,
				// we should be Ok
				Folder folder = new Folder( parentFolderId, projectId, folderName, 
						folderDescription, 0, user.getEmailId(), databaseType);
				request.setAttribute("folder", folder);
				
			}
			else {
				// multiple folders.
				
				System.out.println("srt in create multiple folders");
				System.out.println("srt in create multiple folders folderName is " + folderName);
				System.out.println("srt in create multiple folders folderDescription is " + folderDescription );
				String[] folderNames = folderName.split("#");
				String[] folderDescriptions = folderDescription.split("#");
				
				for (int i= 0; i<folderNames.length; i++){
					try{
						folderName = folderNames[i];
						folderDescription = folderDescriptions[i];
						
						System.out.println("srt in create multiple folders i is " + i);
						
						System.out.println("srt in create multiple folders folderName is " + folderName);
						System.out.println("srt in create multiple folders folderDescription is " + folderDescription );
						
						folderName = folderName.replace('\'', '^');
						folderName = folderName.replace('"', '^');
						folderName = folderName.replace("::", "--");
						
						folderDescription = folderDescription.replace('\'', '^');
						folderDescription = folderDescription.replace('"', '^');
						folderDescription = folderDescription.replace("::", "--");
						folderDescription = folderDescription.replace('\n', ' ');
						folderDescription = folderDescription.replace('\r', ' ');
						
			
			
						if (folderName.contains("/")){
							// this is a request to create folder / subfolders
							// example create  a folder called levelA/levelB/levelC 
							try{
								String[] subFolders = folderName.split("/");
								String[] subFolderDescriptions = folderDescription.split("/");
								
								// lets check if level 0 folder exists nad it not create one. 
								Folder level0Folder = null;
								if (subFolders.length > 0 ){
									String level0FolderName = subFolders[0];
									String level0FolderPath = parentFolder.getFolderPath() + "/" + level0FolderName;
									String level0FolderDescription = "";
									
									if (subFolderDescriptions.length > 0 ){
										level0FolderDescription = subFolderDescriptions[0];
									}
									else {
										level0FolderDescription = level0FolderName;
									}
									// lets check if this folder exists adn if not create it. 
									System.out.println("srt Lets see if Level 0 folder exists " + level0FolderPath );
									level0Folder = new Folder(level0FolderPath, projectId);
									if ( level0Folder.getFolderId() == 0){
										// folder doesn't exist. so lets create it 
										System.out.println("srt : Level 0 folder doesn't exist . Creating a new one " + level0FolderPath );
										level0Folder = new Folder( parentFolderId, projectId, level0FolderName, 
												level0FolderDescription, 0, user.getEmailId(), databaseType);
									}
									
									// at this point, level 0 exists. 
									
									
									
									// lets check if level 1 folder exists and it not create one. 
									Folder level1Folder = null;
									if (subFolders.length > 1 ){
										String level1FolderName = subFolders[1];
										String level1FolderPath = level0Folder.getFolderPath() + "/" + level1FolderName;
										String level1FolderDescription = "";
										
										if (subFolderDescriptions.length > 1 ){
											level1FolderDescription = subFolderDescriptions[1];
										}
										else {
											level1FolderDescription = level1FolderName;
										}
										// lets check if this folder exists adn if not create it. 
										System.out.println("srt Lets see if Level 1 folder exists " + level1FolderPath );
										level1Folder = new Folder(level1FolderPath, projectId);
										if ( level1Folder.getFolderId() == 0){
											// folder doesn't exist. so lets create it 
											System.out.println("srt : Level 1 folder doesn't exist . Creating a new one " + level1FolderPath );
											level1Folder = new Folder( level0Folder.getFolderId(), projectId, level1FolderName, 
													level1FolderDescription, 0, user.getEmailId(), databaseType);
										}
										
										// at this point, level 1 exists. 
										
										
										// lets check if level 2 folder exists and it not create one. 
										Folder level2Folder = null;
										if (subFolders.length > 1 ){
											String level2FolderName = subFolders[2];
											String level2FolderPath = level1Folder.getFolderPath() + "/" + level2FolderName;
											String level2FolderDescription = "";
											
											if (subFolderDescriptions.length > 2 ){
												level2FolderDescription = subFolderDescriptions[2];
											}
											else {
												level2FolderDescription = level2FolderName;
											}
											// lets check if this folder exists adn if not create it. 
											System.out.println("srt Lets see if Level 2 folder exists " + level2FolderPath );
											level2Folder = new Folder(level2FolderPath, projectId);
											if ( level2Folder.getFolderId() == 0){
												// folder doesn't exist. so lets create it 
												System.out.println("srt : Level 2 folder doesn't exist . Creating a new one " + level2FolderPath );
												level2Folder = new Folder( level1Folder.getFolderId(), projectId, level2FolderName, 
														level2FolderDescription, 0, user.getEmailId(), databaseType);
											}
											
											// at this point, level 2 exists.
										} // end of level 2
									}// end of level 1
									
								}// end of level 0
							}
							catch (Exception parentChildFolderException){
								parentChildFolderException.printStackTrace();
							}
						}// end of multi level (parent / child  folder creation)
						else {
							// this is a requests to create a folder (Single folder)
							// for all sub folders (ie non root level folders), we default the 
							// folderOrder to 0. Since we use the sorting by folder_order, folder_name,
							// we should be Ok
							Folder folder = new Folder( parentFolderId, projectId, folderName, 
									folderDescription, 0, user.getEmailId(), databaseType);
							
							System.out.println("srt in create multiple folder has been created  " + folder.getFolderPath() );
							request.setAttribute("folder", folder);
						}
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}
				
			}
			
			// Once the folder is created, the project structure has changed and the project object in memory is no longer
			// valid.So, we need to create a new one and replace the one in the session memory.
			project = new Project(projectId, databaseType);
			session.setAttribute("project", project);
			
			// Same with the security privs. we need to reset them in the session, so that this user
			// can work on these newly created folders. 
			
			securityProfile = new SecurityProfile(user.getUserId(),this.getServletContext().getInitParameter("databaseType"));
			session.setAttribute("securityProfile", securityProfile);
			
			
			
			//we will try forwarding to returnFolderId jsp and the AJAX will call displayRealFolder.jsp with the folder Id after that.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Folder/returnFolderId.jsp");
			//RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Folder/displayRealFolder.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if (action.equals("deleteFolder")){
			// we need to deleted the folder and refresh the project object in memory.
			String folderIdString = request.getParameter("folderId");
			int folderId = Integer.parseInt(folderIdString);
			FolderUtil.deleteFolder(project.getProjectId(), folderId, user.getEmailId(),  databaseType);
			
			// Once the folder is created, the project structure has changed and the project object in memory is no longer
			// valid. So, we need to create a new one and replace the one in the session memory.
			// First we get the project id of the stale project object.
			int projectId = project.getProjectId();
			
			// Next, we create a new one, and replace it in the session.
			project = new Project(projectId, databaseType);
			session.setAttribute("project", project);
			
			// Now forward it to the dispatcher.
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Folder/deleteFolder.jsp");
			dispatcher.forward(request, response);
			return;
		}
		else if ( action.equals("editFolder")){
			
			
			String folderIdString = request.getParameter("folderId");
			int folderId = Integer.parseInt(folderIdString);
			
			String folderName = request.getParameter("folderName");
			String folderDescription = request.getParameter("folderDescription");
			
			// NOTE : YUI has issues in explorer if the name  has ' or ". so replacing
			// them with ^.
			// Same with folderDescription.
			folderName = folderName.replace('\'', '^');
			folderName = folderName.replace('"', '^');
			folderName = folderName.replace("::", "--");
			
			folderDescription = folderDescription.replace('\'', '^');
			folderDescription = folderDescription.replace('"', '^');
			folderDescription = folderDescription.replace("::", "--");
			folderDescription = folderDescription.replace('\n', ' ');
			folderDescription = folderDescription.replace('\r', ' ');
			

			// At some point once we get user authentication, you may want to send in created by, last modified by.
			Folder folder = new Folder(folderId);
			folder.setNameAndDescription(folderId, folderName, folderDescription);
			
			request.setAttribute("folder", folder);
			
			//we will try forwarding to returnFolderId jsp and the AJAX will call displayRealFolder.jsp with the folder Id after that.
			request.setAttribute("updated", "yes");
			RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Folder/editFolderForm.jsp?folderId=" + folderId);
			dispatcher.forward(request, response);
			return;
		}

		// if nothing else works, forward to the Welcome screen. This should never happen.
		RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/welcome.jsp");
		dispatcher.forward(request, response); 
	
	}

}
