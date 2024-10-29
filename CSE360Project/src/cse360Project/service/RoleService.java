package cse360Project.service;

import cse360Project.repository.RoleRepository;

public class RoleService {

	private RoleRepository roleRepository;
	
	public RoleService(RoleRepository roleRepository)
	{
		this.roleRepository = roleRepository;
	}
	
	// Container for business logic as roles evolve
}
