package cse360Project.service;

import java.sql.SQLException;
import java.util.List;

import cse360Project.Session;
import cse360Project.model.Invitation;
import cse360Project.repository.InvitationRepository;
import cse360Project.repository.RoleRepository;

public class InvitationService {
    private final InvitationRepository invitationRepo;
    private final RoleRepository roleRepo;

    public InvitationService(InvitationRepository invitationRepo, RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
        this.invitationRepo = invitationRepo;
    }

    public boolean validateInvitationCode(String inviteCode) throws SQLException {
        // Check if the invitation exists and is not used
        Invitation invitation = invitationRepo.getInvitationByCode(inviteCode);
        if (invitation != null && !invitation.isUsed()) {
            // Mark the invitation as used
            invitationRepo.markAsUsed(inviteCode);

            // Retrieve all associated roles for this invitation
            List<String> roleNames = roleRepo.getRoleNamesByInvitationCode(inviteCode);

            // Store the roles in the session
            Session session = Session.getInstance();
            session.setInvitedRoles(roleNames);

            return true; // Invitation code is valid and marked as used
        }
        return false; // Invitation code is invalid or already used
    }
}