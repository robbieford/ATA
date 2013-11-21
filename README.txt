Name: Taggart Ashby, Robert Larson-Campbell
Email: tgashby@calpoly.edu, rfcampbe@calpoly.edu
Class/Section: CPE480-03

Assignment 2: Wumpus World Agent
For this assignment we implemented an agent to navigate a world with Wumpuses, Minions, and Pits. This provided an additional challenge from the other labs and assignments where there was no danger or possibility of being "killed".

In order to implement the agent, we used the belief system that was built in to help keep track of information we've gathered thusfar.

The way our agent works is by exploring using depth-first search to search nodes that are not "dangerous". A "dangerous" node is any node that is near a pit. If we encounter a "breeze" we immediately backtrack to a safe node and continue exploring.

In order to determine the route to the next safe node we've implemented a HashMap of paths to each Node we've visted. When we need to backtrack we search for a common base-node in the path, backtrack to that node and then continue along the path to the safe node. This way we never encounter an unsafe node that has not been previously marked as safe.

In the event that we encounter a Minion or a Wumpus, we fire arrows in all directions. If it's a Wumpus we stop firing when we get HIT_WUMPUS return. We don't run the same check for Minions because there's a chance we are in a Node that is bordered by more than one Minion.

For each movement step we set the Safe belief on the current node.

If we are near the gold we ask for the gold direction hint and move forward once. Other than this situation, we never use any hints.

Acknowledged shortcomings:
If the gold is located through a wall of pits with a single hole in it, the agent will never find the gold.

If our agent were unable to find the gold without going through a dangerous node (which it will never do) it will eventually just stop moving after it's explored all the safe nodes.

The Agent does not make use of all of the percepts available to it. It makes fairly dumb straightforward decisions rather than extremely intelligent ones. However it still finds the goal on every provided map.