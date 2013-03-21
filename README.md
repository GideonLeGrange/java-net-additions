java-net-additions
==================

A (small) library of Java networking routines to support some things missing from java.net

The purpose of this library is to easily do some things which are missing or not considered relevant by the core java.net libraries. 
Currently this includes working with networks (as opposed to InetAddress objects). 

* Networks can be defined, and one can check if they contain another network or an IP address.
* A larger network enclosing a given network can be optained. 
* The network directly above or below a network can be obtained. 

There is also a construct for working with IP access lists:
* IP access lists can be constructed. 
* IP access lists can be queried to see if a certain address mataches the list. 

The library supports both IPv4 and IPv6 networks.


LICENSE
=======
java-net-additions is released under the Apache 2.0 license except as otherwise noted.
http://www.apache.org/licenses/LICENSE-2.0
