package me.legrange.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is an network access list tree that can contain permit and deny
 * policies and match a specific IP address against the policies
 */
public class IPAccessList implements java.io.Serializable {

    /**
     * Create a new access list with the given default policy
     */
    public IPAccessList(boolean policy) {
        v4 = new Node(IPv4Network.ALL, policy);
        v6 = new Node(IPv6Network.ALL, policy);
    }

    public void add(String network, int mask, boolean policy) throws NetworkException {
        try {  
            InetAddress addr = InetAddress.getByName(network);
            if (addr instanceof Inet4Address) {
                add(IPv4Network.getByAddress(network, mask), policy);
            }
            else if (addr instanceof Inet6Address) {
                add(IPv6Network.getByAddress(network, mask), policy);
            }
        } catch (UnknownHostException ex) {
            throw new InvalidAddressException(ex.getMessage(), ex);
        }
    }

    public void add(IPNetwork network, boolean policy) throws NetworkException {
        Node place = findPlace(network);
        if (place.getPolicy() != policy) {
            place.addChild(new Node(network, policy));
        }
    }

    public boolean checkAccess(IPNetwork net) throws NetworkException {
        return findPlace(net).getPolicy();
    }

    public boolean checkAccess(InetAddress addr) throws InvalidAddressException {
        if (addr instanceof Inet4Address) {
            try {
                return checkV4Access(addr.getHostAddress());
            } catch (NetworkException ex) {
                throw new InvalidAddressException(ex.getMessage(), ex);
            }
        } else if (addr instanceof Inet6Address) {
            try {
                return checkV6Access(addr.getHostAddress());
            } catch (NetworkException ex) {
                throw new InvalidAddressException(ex.getMessage(), ex);
            }
        }
        return false;
    }

    private boolean checkV4Access(String ip) throws NetworkException {
        return checkAccess(IPv4Network.getByAddress(ip, 32));
    }

    private boolean checkV6Access(String ip) throws NetworkException {
        return checkAccess(IPv6Network.getByAddress(ip, 128));
    }

    /**
     * find the correct parent node for the given network
     */
    private Node findPlace(IPNetwork net) throws NetworkException {
        if (net instanceof IPv4Network) {
            return findPlace(net, v4);
        }
        return findPlace(net, v6);
    }

    /**
     * find the correct parent node for the given network relative to the given
     * root
     */
    private Node findPlace(IPNetwork network, Node root) throws NetworkException {
        for (Node child : root.getChildren()) {
            if (child.getNetwork().containsAddress(network.getAddress())) {
                return findPlace(network, child);
            }
        }
        return root;
    }

    /**
     * A node in the access tree
     */
    private static class Node {

        /**
         * create a new node
         */
        private Node(IPNetwork network, boolean policy) {
            this.network = network;
            this.policy = policy;
        }

        /**
         * return the children for this node
         */
        private List<Node> getChildren() {
            return children;
        }

        /**
         * return the network for this node
         */
        private IPNetwork getNetwork() {
            return network;
        }

        /**
         * return the policy for this node
         */
        private boolean getPolicy() {
            return policy;
        }

        /**
         * add a child to this node
         */
        private void addChild(Node child) throws NetworkException {
            children.add(child);
            for (Iterator<Node> it = children.iterator(); it.hasNext();) {
                Node have = it.next();
                if (have == child) {
                    continue;
                }
                if (child.getNetwork().containsAddress(have.getNetwork().getAddress())) {
                    it.remove();
                    child.addChild(have);
                }
            }
        }
        private IPNetwork network;
        private boolean policy;
        private List<Node> children = new ArrayList<>();
    }
    private Node v4;
    private Node v6;
}
