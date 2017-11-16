import java.io.*;

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

/* Class constructor */
public Node() {
}

/* students to write the following two routines, and maybe some others */
void rtinit(int nodename, int[] initial_lkcost) {
        //initialize name costs and direct neighbors of a node
        this.nodename = nodename;
        this.lkcost = initial_lkcost;
        this.neighbors = new boolean[this.lkcost.length];
        // loop through original link costs and figure out which nodes are neighbors
        for(int i=0; i < initial_lkcost.length; i++) {
                // if infinity then the nodes arent neighbors and if 0 the node is itself
                if((initial_lkcost[i] != INFINITY) && (initial_lkcost[i] != 0)) {
                        this.neighbors[i] = true;
                }
                else{
                        this.neighbors[i]=false;
                }
        }
        // initialize and fill cost table with INFINITY
        initializeCosts();
        // send current link costs to all neighbors of this node
        sendToNeighbors(this.lkcost);
        //printdt();
}

void rtupdate(Packet rcvdpkt) {
        // get relevant information from packet such as source of packet and delay to this node
        int fromNode = rcvdpkt.sourceid;
        int delaytofromNode = this.lkcost[fromNode];
        //loop through min cost Vector
        int[] mincost = rcvdpkt.mincost;
        // this.node is the current node you are updating i is the specific distance you are evaluating and fromNode is the via node
        for (int i =0; i < mincost.length; i++) {
          // if you find a better distance and you are not referencing yourself then make a change to the costs
          if( (this.nodename != i) && (delaytofromNode + mincost[i] < this.costs[i][fromNode]) ){
            System.out.println("Delay from "+this.nodename+" to "+fromNode+ " is "+delaytofromNode+
                                " and from "+fromNode+" to "+i+" is "+(mincost[i])+
                                " totaling "+(delaytofromNode + mincost[i])+
                                " which is less than the previous delay of "+this.costs[i][fromNode]);
            this.costs[i][fromNode] = (delaytofromNode + mincost[i]);
          }
        }
        printdt();
        System.out.println("\n");
        //send out a new packet based on the new costs
        if(needToUpdateLK()){
          System.out.println("LK updated sending more packets!!!!!!!!!");
          sendToNeighbors(this.lkcost);
        }

}


/* called when cost from the node to linkid changes from current value to newcost*/
void linkhandler(int linkid, int newcost) {
        System.out.println("Something has changed: "+linkid+" to "+newcost);
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
// prints out this nodes direct neighbors
void printNeighbors(){
        System.out.print("Node "+this.nodename+" has neighbors: ");
        for (int i=0; i < neighbors.length; i++ ) {
                if(this.neighbors[i]){
                  System.out.print(" "+i);
                }

        }
        System.out.print("\n");

}

//initializes the costs table
void initializeCosts(){
  this.costs = new int[this.lkcost.length][this.lkcost.length];
  // initially says you cannot get anywhere
  for (int i = 0; i < this.costs.length ; i++ ) {
    for(int j = 0; j < this.costs[i].length; j++){
      this.costs[i][j] = INFINITY;
    }
  }
  //this.costs[this.nodename][this.nodename] = 0;
  //printCosts();
}

void printCosts(){
  System.out.println("Full costs matrix for "+this.nodename);
  for (int i = 0; i < this.costs.length ; i++ ) {
    for(int j = 0; j < this.costs[0].length; j++){
        System.out.print(" "+this.costs[i][j]);
    }
    System.out.print("\n");
  }
}

// send packet to neighbors
void sendToNeighbors(int[] mincost){
  for (int i=0; i < this.neighbors.length; i++) {
    if(this.neighbors[i]){
      //creates new packet
      Packet sndpkt = new Packet(nodename, i, mincost);
      //sends the newly made packt to each neighbor i
      NetworkSimulator.tolayer2(sndpkt);

    }
  }
}

// checs the current minimum link costs vs the tables link costs and fixes changes if they exist
boolean needToUpdateLK(){
  boolean altered = false;

  for (int i=0; i < this.lkcost.length; i++) {
    for (int j=0; j < this.costs[0].length ;j++ ) {
      if(this.costs[i][j] < this.lkcost[i]){
        System.out.println("INCONSISTENCY FOUND");
        System.out.println("Current delay from "+this.nodename+" to "+i+" is "+this.lkcost[i]);
        System.out.println("Table has delay from "+this.nodename+" to "+i+" as "+this.costs[i][j]+" via "+j);
        System.out.println("\n");
        this.lkcost[i] = this.costs[i][j];
        altered = true;
      }
    }
  }

  return altered;
}


}
