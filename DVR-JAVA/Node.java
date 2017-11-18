import java.io.*;
import java.util.Arrays;
/**
 * This is the class that students need to implement. The code skeleton is provided.
 * Students need to implement rtinit(), rtupdate() and linkhandler().
 * printdt() is provided to pretty print a table of the current costs for reaching
 * other nodes in the network.
 */
public class Node {

public static final int INFINITY = 9999;

int[] lkcost;       /*The link cost between this node and other nodes*/
int[][] costs;          /*Define distance table*/
int nodename;                   /*Name of this node*/
boolean[] neighbors;            /*when initialized records who is a direct neighbor*/
int[] altlkcost;


/* Class constructor */
public Node() {
}

/* students to write the following two routines, and maybe some others */
void rtinit(int nodename, int[] initial_lkcost) {
        //initialize name of node and the original link costs to direct neighbors
        this.nodename = nodename;
        this.lkcost = initial_lkcost;
        System.out.println("Initializing node "+this.nodename);
        //used to keep track of the current best path to a certain node from this node
        this.altlkcost = Arrays.copyOf(initial_lkcost, initial_lkcost.length);
        // can have at most this.lkcost.length neighbors
        this.neighbors = new boolean[this.lkcost.length];
        // loop through original link costs and figure out which nodes are neighbors and which are not
        for(int i=0; i < initial_lkcost.length; i++) {
                // if infinity then the nodes arent neighbors and if 0 the node is itself
                if((initial_lkcost[i] != INFINITY) && (initial_lkcost[i] != 0)) {
                        this.neighbors[i] = true;
                }
                else{
                        this.neighbors[i]=false;
                }
        }
        // initialize and fill cost table with INFINITY to be later over written
        initializeCosts();
        // send current link costs to all neighbors of this node
        sendToNeighbors(this.altlkcost);
        //printdt();
}

void rtupdate(Packet rcvdpkt) {
        // get the senders id
        int fromNode = rcvdpkt.sourceid;
        // gets the delay from this node to its neighbor sender
        int delaytofromNode = this.lkcost[fromNode];
        // gets the mincost array from the sender node
        //loop through min cost Vector
        int[] mincost = rcvdpkt.mincost;
        //registers the fact that no changes have been made to the costs or current mincosts yet
        boolean changed = false;
        // this.node is the current node you are updating i is the specific distance you are evaluating and fromNode is the via node
        // i is the destination node from node is the via node and this.nodemane is the current node which costs and link costs you are changing
        for (int i =0; i < mincost.length; i++) {
                // if you find a different distance and you are not looking at the mincost from via node to yourself
                if((this.nodename != i) && ((delaytofromNode + mincost[i])!=this.costs[i][fromNode])) {
                        //printLK();
                        // helps with debuggins
                        System.out.println("Delay from "+this.nodename+" to "+fromNode+ " is "+delaytofromNode+
                                           " and from "+fromNode+" to "+i+" is "+(mincost[i])+
                                           " totaling "+(delaytofromNode + mincost[i])+
                                           " and the previous delay of "+this.costs[i][fromNode]);
                        //if you are getting a new best distance from this node to i via from node based on the delay to the via node and the minimum cost from via node to destination i
                        this.costs[i][fromNode] = (delaytofromNode + mincost[i]);
                        //mark the fact that something is now different in the costs table and you will need to re evaluate the min cost for a specific node and inform neighbors
                        changed = true;
                }
        }
        //for debuggins
        printdt();
        System.out.println("\n");
        //if something has changed in teh costs table run the need to update neighbors method and print info to user
        if(changed) {
                System.out.println("Costs table was changed need to send out new DV");
                needToUpdate();
        }
        // if(needToUpdateLK()) {
        //         System.out.println("LK updated sending more packets!!!!!!!!!");
        // }

}


/* called when cost from the node to linkid changes from current value to newcost*/
void linkhandler(int linkid, int newcost) {
        // tells user that the link between this node and linkid is being changed to new cost
        System.out.println("\nDelay from "+this.nodename+" to "+linkid+" is being changed from "+this.lkcost[linkid]+" to "+newcost+"\n");
        //updates the link costs to include this new link value
        this.lkcost[linkid] = newcost;
        //resets the current minimum costs too because everything will now be different
        this.altlkcost = Arrays.copyOf(this.lkcost, this.lkcost.length);
        // whip this nodes costs table you have and regain costs information
        initializeCosts();
        // send current link costs to all neighbors of this node
        sendToNeighbors(this.lkcost);

}


/* Prints the current costs to reaching other nodes in the network */
void printdt() {
        switch(this.nodename) {

        case 0:
                System.out.printf("                via     \n");
                System.out.printf("   D0 |    1     2 \n");
                System.out.printf("  ----|-----------------\n");
                System.out.printf("     1|  %3d   %3d \n",this.costs[1][1], this.costs[1][2]);
                System.out.printf("dest 2|  %3d   %3d \n",this.costs[2][1], this.costs[2][2]);
                System.out.printf("     3|  %3d   %3d \n",this.costs[3][1], this.costs[3][2]);
                break;
        case 1:
                System.out.printf("                via     \n");
                System.out.printf("   D1 |    0     2    3 \n");
                System.out.printf("  ----|-----------------\n");
                System.out.printf("     0|  %3d   %3d   %3d\n",this.costs[0][0], this.costs[0][2],this.costs[0][3]);
                System.out.printf("dest 2|  %3d   %3d   %3d\n",this.costs[2][0], this.costs[2][2],this.costs[2][3]);
                System.out.printf("     3|  %3d   %3d   %3d\n",this.costs[3][0], this.costs[3][2],this.costs[3][3]);
                break;
        case 2:
                System.out.printf("                via     \n");
                System.out.printf("   D2 |    0     1    3 \n");
                System.out.printf("  ----|-----------------\n");
                System.out.printf("     0|  %3d   %3d   %3d\n",this.costs[0][0], this.costs[0][1],this.costs[0][3]);
                System.out.printf("dest 1|  %3d   %3d   %3d\n",this.costs[1][0], this.costs[1][1],this.costs[1][3]);
                System.out.printf("     3|  %3d   %3d   %3d\n",this.costs[3][0], this.costs[3][1],this.costs[3][3]);
                break;
        case 3:
                System.out.printf("                via     \n");
                System.out.printf("   D3 |    1     2 \n");
                System.out.printf("  ----|-----------------\n");
                System.out.printf("     0|  %3d   %3d\n",this.costs[0][1],this.costs[0][2]);
                System.out.printf("dest 1|  %3d   %3d\n",this.costs[1][1],this.costs[1][2]);
                System.out.printf("     2|  %3d   %3d\n",this.costs[2][1],this.costs[2][2]);
                break;
        }
}

// prints out this nodes direct neighbors by loop through neighbors
// this nodes neighbors should never change
void printNeighbors(){
        System.out.print("Node "+this.nodename+" has neighbors: ");
        for (int i=0; i < neighbors.length; i++ ) {
                if(this.neighbors[i]) {
                        System.out.print(" "+i);
                }

        }
        System.out.print("\n");

}

//initializes the costs table to be infinity
void initializeCosts(){
        this.costs = new int[this.lkcost.length][this.lkcost.length];
        // initially says you cannot get anywhere
        for (int i = 0; i < this.costs.length; i++ ) {
                for(int j = 0; j < this.costs[i].length; j++) {
                        this.costs[i][j] = INFINITY;
                }
        }

}

//prints out the original link costs for a node and the current best distance to each other node
void printLK(){
        System.out.print(nodename+" link costs: ");
        for (int i = 0; i < this.lkcost.length; i++ ) {
                System.out.print(" " +this.lkcost[i]);
        }
        System.out.print("\n");
        System.out.print(nodename+" minimum link costs: ");
        for (int i = 0; i < this.altlkcost.length; i++ ) {
                System.out.print(" " +this.altlkcost[i]);
        }
        System.out.print("\n");
}

//helps with debuggins will print out the entire costs matrix
void printCosts(){
        System.out.println("Full costs matrix for "+this.nodename);
        for (int i = 0; i < this.costs.length; i++ ) {
                for(int j = 0; j < this.costs[0].length; j++) {
                        System.out.print(" "+this.costs[i][j]);
                }
                System.out.print("\n");
        }
}

// send packet to neighbors
void sendToNeighbors(int[] mincost){
        // go through every neighbor
        for (int i=0; i < this.neighbors.length; i++) {
                // if this node and node i are neighbors
                if(this.neighbors[i]) {
                        // this is for the poison stuff creates a new array that is a copy of the current min costs
                        int[] splithorizon = Arrays.copyOf(this.altlkcost, this.altlkcost.length);
                        // loop through destinations in costs table
                        for (int k = 0; k < this.costs.length; k++ ) {
                                //find minimum value
                                int tempmin = INFINITY;
                                //loop through cost from this node to node i via nodes j
                                for(int j = 0; j < this.costs[0].length; j++) {
                                        //if this node sends a packet to node i saying that going through j on your way to k causes a loop returns true dont touch this value
                                        if(causesloop(this.nodename, i, j, k)) {
                                                continue;
                                        }
                                        // if this node sending node i a packet with mincost from this node to k via j doesnt cause a loop
                                        // and the recieving node doesnt equal the destination and the destination isnt this node and the via node is not the destination
                                        // and the cost from this node to k via j is less then the current best distance update the tempmin
                                        else if( (k!=i) && (k != this.nodename) && (j!=i)&&(this.costs[k][j] < tempmin)) {
                                                tempmin = this.costs[k][j];
                                        }
                                }
                                // if the tempmin has been changed update the the split horizon which is a copy of the mincosts if it has not been updated the current value you have is fine to send to node i
                                if(tempmin != INFINITY) {
                                        splithorizon[k] = tempmin;
                                }

                        }
                        //this was all for debbuging
                        //splithorizon[i] = INFINITY;
                        //System.out.println("Sending packet from "+this.nodename+" to "+i+" with min costs of");
                        //printintarray(splithorizon);
                        //creates new packet
                        Packet sndpkt = new Packet(this.nodename, i, splithorizon);
                        //sends the newly made packt to each neighbor i
                        NetworkSimulator.tolayer2(sndpkt);

                }
        }
}

// returns nothing but updates minimum costs from this node to another node
void needToUpdate(){
        // loop through the current minimum costs
        for (int i=0; i < this.lkcost.length; i++) {
                //loop through current costs table at row i corresponding to the link cost
                for (int j=0; j < this.costs[0].length; j++ ) {
                        //if there exists a a way to get to node i via j that is faster than the current minimum cost report that an INCONSISTENCY was found
                        if(this.costs[i][j] < this.altlkcost[i]) {
                                //helps with debbuging
                                System.out.println("INCONSISTENCY FOUND");
                                System.out.println("Current delay from "+this.nodename+" to "+i+" is "+this.altlkcost[i]);
                                System.out.println("Table has delay from "+this.nodename+" to "+i+" as "+this.costs[i][j]+" via "+j+"\n");
                                // updated min costs
                                this.altlkcost[i] = this.costs[i][j];
                        }
                }
        }
        // tell all neighbors about changes
        sendToNeighbors(this.altlkcost);

}

// checs the current minimum link costs vs the tables link costs and fixes changes if they exist and returns true if fixes need to be made
boolean needToUpdateLK(){
        boolean altered = false;

        // loop through the current minimum costs
        for (int i=0; i < this.lkcost.length; i++) {
                //loop through current costs table at row i corresponding to the link cost
                for (int j=0; j < this.costs[0].length; j++ ) {
                        //if there exists a a way to get to node i via j that is faster than the current minimum cost report that an INCONSISTENCY was found
                        if(this.costs[i][j] < this.altlkcost[i]) {
                                //helps with debbuging
                                System.out.println("INCONSISTENCY FOUND");
                                System.out.println("Current delay from "+this.nodename+" to "+i+" is "+this.altlkcost[i]);
                                System.out.println("Table has delay from "+this.nodename+" to "+i+" as "+this.costs[i][j]+" via "+j+"\n");
                                // mark the new fastest route
                                this.altlkcost[i] = this.costs[i][j];
                                // mark that a change has been made so this node will know to resend out vecotrs to neighbors
                                altered = true;
                        }
                }
        }
        //if there is a faster way to get to a node than the current minimum cost then resend vectors to neighbors
        if(altered) {
                sendToNeighbors(this.altlkcost);
        }
        // used for printing
        return altered;
}

//helper function is more of a bandaid than anything else basically will detect a loop when calculating what costs to send a specific node from another node
//needs to be better and more general
boolean causesloop(int sender, int reciever, int via, int destination){
        if((sender == 1)&&(reciever == 2)&&(via == 0)&&(destination == 3)) {
                return true;
        }
        if((sender == 1)&&(reciever == 2)&&(via == 3)&&(destination == 0)) {
                return true;
        }
        if((sender == 2)&&(reciever == 1)&&(via == 0)&&(destination == 3)) {
                return true;
        }
        if((sender == 2)&&(reciever == 1)&&(via == 3)&&(destination == 0)) {
                return true;
        }
        if((reciever == via) && (via != destination)){
          //System.out.println("\n\nIM HERE\n\n");
          return true;
        }

        return false;
}

// simple iterator to print out arrays for testing and debbuging
void printintarray(int[] ar){
        for (int i = 0; i < ar.length; i++ ) {
                System.out.print(" "+ar[i]);
        }
        System.out.print("\n");
}

}
