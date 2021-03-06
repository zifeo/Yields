package yields.client.node;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import yields.client.id.Id;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Connected user who will do the connexion with the outside world.
 */
public class ClientUser extends User {

    private List<Group> mGroups;
    private List<Group> mCommentGroups;
    private final List<User> mEntourage;
    private final List<Group> mNodes;

    /**
     * Creates a client user which represents the client connected to the application.
     *
     * @param name  The name of the User.
     * @param id    The id of the User.
     * @param email The Email of the User.
     * @param img   The image of the User.
     */
    public ClientUser(String name, Id id, String email, Bitmap img) {
        super(name, id, email, img);
        mGroups = new ArrayList<>();
        mEntourage = new ArrayList<>();
        mNodes = new ArrayList<>();
        mCommentGroups = new ArrayList<>();
    }

    /**
     * constructs a shell for a clientUser waiting for update from server.
     *
     * @param email The email of the user.
     */
    public ClientUser(String email) {
        this("", new Id(0l), email, YieldsApplication.getDefaultUserImage());
    }

    /**
     * Add groups to the user
     *
     * @param groups the groups to add
     */
    public void addGroups(List<Group> groups) {
        for (Group group : groups) {
            addGroup(group);
        }
    }

    /**
     * Add a group to the user
     *
     * @param group the group to add.
     */
    public void addGroup(Group group) {
        for (Group prevGroup : mGroups) {
            if (prevGroup.getId().getId().equals(group.getId().getId())) {
                return;
            }
        }
        mGroups.add(group);
    }

    /**
     * Add a group to the user
     *
     * @param node the node to add.
     */
    public void addNode(Group node) {
        if (node.getId().getId() != 0) {
            for (Group prevGroup : mGroups) {
                if (prevGroup.getId().getId().equals(node.getId().getId())) {
                    return;
                }
            }
            for (Node prevGroup : mNodes) {
                if (prevGroup.getId().getId().equals(node.getId().getId())) {
                    return;
                }
            }
        }
        mNodes.add(node);
    }

    /**
     * Add a comment group to the user
     *
     * @param group The comment group to add.
     */
    public void addCommentGroup(Group group) {
        for (Group prevGroup : mCommentGroups) {
            if (prevGroup.getId().getId().equals(group.getId().getId())) {
                return;
            }
        }
        mCommentGroups.add(group);
    }

    /**
     * Remove a group from the user's list.
     *
     * @param group the group to remove.
     */
    public void removeGroup(Group group) {
        mGroups.remove(Objects.requireNonNull(group));
    }

    /**
     * Get an unmodifiable sorted list of the user groups.
     *
     * @return The sus-mentioned list.
     */
    public List<Group> getUserGroups() {
        Collections.sort(mGroups, mComparator);
        return new ArrayList<>(mGroups);
    }

    /**
     * Adds a User to the entourage.
     *
     * @param user the user to add.
     */
    public void addUserToEntourage(User user) {
        mEntourage.add(user);
    }

    /**
     * Remove a User from the entourage.
     *
     * @param user the user to remove.
     */
    public void removeUserFromEntourage(User user) {
        mEntourage.remove(Objects.requireNonNull(user));
    }

    /**
     * Gets an unmodifiable list of users.
     *
     * @return The list of user connected to this user.
     */
    public List<User> getEntourage() {
        return new ArrayList<>(mEntourage);
    }

    /**
     * Activates the group when we received confirmation from the server.
     *
     * @param id The id of the group to activate.
     */
    public void activateGroup(Date ref, Id id) {
        Group group = getGroupFromRef(ref);
        group.setId(id);
        group.setValidated();
        group.setLastUpdate(new Date());
    }

    /**
     * Getter for a Group by it's ID, returns null if there is no group for
     * the given Id.
     *
     * @param groupId The Id of the wanted Group.
     * @return The Group or null if there is no such Group.
     */
    public Group getGroup(Id groupId) {
        for (Group group : mGroups) {
            if (group.getId().equals(groupId)) {
                return group;
            }
        }
        return getCommentGroup(groupId);
    }

    /**
     * Getter for a Comment Group by it's ID, returns null if there is no group for
     * the given Id.
     *
     * @param groupId The Id of the wanted comment Group.
     * @return The Group or null if there is no such comment Group.
     */
    public Group getCommentGroup(Id groupId) {
        for (Group group : mCommentGroups) {
            if (group.getId().equals(groupId)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Getter for a User from the entourage.
     * Returns null if the Id of the User is not in the entourage.
     *
     * @param userId The id of the User.
     * @return The User from the entourage with the same id or null
     * if there is no User with that Id in the entourage.
     */
    public User getUserFromEntourage(Id userId) {
        for (User user : mEntourage) {
            if (userId.equals(user.getId())) {
                return user;
            }
        }

        return null;
    }

    /**
     * Getter for a Node from the connected nodes.
     * Returns null if the Id of the Node is not in the entourage.
     *
     * @param nodeId The id of the Node.
     * @return The User from the entourage with the same id or null
     * if there is no User with that Id in the entourage.
     */
    public Group getNodeFromId(Id nodeId) {
        for (Group node : mNodes) {
            if (nodeId.equals(node.getId())) {
                return node;
            }
        }

        for (Group node : mGroups) {
            if (nodeId.equals(node.getId())) {
                return node;
            }
        }

        return null;
    }

    /**
     * Getter for a Node from the connected nodes.
     * Returns null if the Id of the Node is not in the entourage.
     *
     * @return The User from the entourage with the same id or null
     * if there is no User with that Id in the entourage.
     */
    public Group getGroupFromRef(Date ref) {
        for (Group node : mNodes) {
            if (node.getRef() != null && node.getRef().compareTo(ref) == 0) {
                return node;
            }
        }
        for (Group group : mGroups) {
            if (group.getRef() != null && group.getRef().compareTo(ref) == 0) {
                return group;
            }
        }

        return null;
    }

    /**
     * The group comparator.
     */
    private final Comparator<Group> mComparator = new Comparator<Group>() {

        @Override
        public int compare(Group lhs, Group rhs) {
            return rhs.getLastUpdate().compareTo(lhs.getLastUpdate());
        }
    };

}
