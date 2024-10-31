package cse360Project.service;

import cse360Project.repository.RoleRepository;

/*******
 * <p> RoleService Class </p>
 * 
 * <p> Description: Service class which deals with business logic on top of the data layer. </p>
 * 
 * <p> Copyright: Carlos Hernandez © 2024 </p>
 * 
 * @author Carlos Hernandez
 * 
 * @version 1.0.0   2024-10-30 Updated for Phase 2
 */

public class RoleService {

	private RoleRepository roleRepository;
	
	public RoleService(RoleRepository roleRepository)
	{
		this.roleRepository = roleRepository;
	}
	
	// Container for business logic as roles evolve
}
