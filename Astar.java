package applications;

import java.util.PriorityQueue;

public class AStar { 

	public static final int DiagonalCost = 14;
	public static final int VHCost = 10;

	private Cell[][] grid;

	private PriorityQueue<Cell> openCells;

	private boolean[][] closedCells;

	private int startI, startJ;
	private int endI, endJ;

	public AStar(int width, int height, int si, int sj, int ei, int ej, int [][] blocks) {
		
		grid = new Cell[width][height];
		closedCells = new boolean[width][height];
		openCells = new PriorityQueue<Cell>((Cell c1, Cell c2) -> {
			return c1.finalCost < c2.finalCost ? - 1 : c1.finalCost > c2.finalCost ? 1 : 0;
		});
		
		startCell(si, sj);
		endCell(ei, ej);
		
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				
				grid[i][j] = new Cell(i, j);
				grid[i][j].heuristicCost = Math.abs(i - endI) + Math.abs(j - endJ);
				grid[i][j].solution = false;
			}
		}
		grid[startI][startJ].finalCost = 0;
		for(int i = 0; i < blocks.length; i++) {
			
			addBlockOnCell(blocks[i][0], blocks[i][1]);
		}
	}
	
	public void addBlockOnCell(int i, int j) {
		grid[i][j] = null;
	}

	public void startCell(int i, int j) {
		startI = i;
		startJ = j;
	}

	public void endCell(int i, int j) {
		endI = i;
		endJ = j;
	}
	
	public void updateCostIfNeeded(Cell current, Cell t, int cost) {
		
		if(t == null || closedCells[t.i][t.j])
			return;
		
		int tFinalCost = t.heuristicCost + cost;
		boolean isOpen = openCells.contains(t);
		
		if(!isOpen || tFinalCost < t.finalCost) {
			t.finalCost = tFinalCost;
			t.parent = current;
			
			if(!isOpen) {
				openCells.add(t);
			}
		}
	}
	
	public void process() {
		
		openCells.add(grid[startI][startJ]);
		Cell current;
		
		while(true) {
			current = openCells.poll();
			
			if(current == null) {break;}
			
			closedCells[current.i][current.j] = true;
			
			if(current.equals(grid[endI][endJ])) { return; }
			
			Cell t;
			
			if(current.i - 1 >= 0) {
				t = grid[current.i - 1][current.j];
				updateCostIfNeeded(current, t, current.finalCost + VHCost);
				
				if(current.j - 1 >= 0) {
					t = grid[current.i - 1][current.j - 1];
					updateCostIfNeeded(current, t, current.finalCost + DiagonalCost);
				}
				if(current.j + 1 < grid[0].length) {
					t = grid[current.i - 1][current.j + 1];
					updateCostIfNeeded(current, t, current.finalCost + DiagonalCost);
				}
			}
			
			if(current.j - 1 >= 0) {
				t = grid[current.i][current.j - 1];
				updateCostIfNeeded(current, t, current.finalCost + VHCost);
			}
			if(current.j + 1 < grid[0].length) {
				t = grid[current.i][current.j + 1];
				updateCostIfNeeded(current, t, current.finalCost + VHCost);
			}
			if(current.i + 1 < grid.length) {
				t = grid[current.i + 1][current.j];
				updateCostIfNeeded(current, t, current.finalCost + VHCost);
				
				if(current.j - 1 >= 0) {
					t = grid[current.i + 1][current.j - 1];
					updateCostIfNeeded(current, t, current.finalCost + DiagonalCost);
				}
				if(current.j + 1 < grid[0].length) {
					t = grid[current.i + 1][current.j + 1];
					updateCostIfNeeded(current, t, current.finalCost + DiagonalCost);
				}
			}
		}
	}
	
	public void display() {
		
		System.out.println("Grid: ");
		
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				
				if(i == startI && j == startJ) {
					System.out.print("SO  ");			//SOURCE CELL
				}
				else if(i == endI && j == endJ) { 
					System.out.println("DE  "); 		//END CELL
				}
				else if(grid[i][j] != null) {
					System.out.printf("%-3d", 0);
				}
				else {
					System.out.println("BL  ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public void displayScores() {
		
		System.out.println("\nScores For Cell:  ");
		
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[i].length; j++) {
				
				if(grid[i][j] != null) {
					System.out.printf("%-3d", grid[i][j].finalCost);
				}
				else {
					System.out.print("BL  ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public void displaySolution() {		//Tracking Back Path
		if(closedCells[endI][endJ]) {
			
			System.out.print("Path: ");
			
			Cell current = grid[endI][endJ];
			System.out.print(current);
			grid[current.i][current.j].solution = true;
			
			while(current.parent != null) {
				
				System.out.print("->" + current.parent);
				grid[current.parent.i][current.parent.j].solution = true;
				current = current.parent;
			}
			System.out.println("\n");
			
			System.out.print("\nScores For Cell:  ");
			
			for(int i = 0; i < grid.length; i++) {
				for(int j = 0; j < grid[i].length; j++) {
					
					if(i == startI && j == startJ) {
						System.out.print("SO  ");			//SOURCE CELL
					}
					else if(i == endI && j == endJ) { 
						System.out.println("DE  "); 		//END CELL
					}
					else if(grid[i][j] != null) {
						System.out.printf("%-3s", grid[i][j].solution ? "X" : "0");
					}
					else {
						System.out.print("BL  ");
					}
					System.out.println();
				}
				System.out.println();
			} 
		} else { System.out.print("No Possible Path"); }	
	}
	
	
	
	
	public static void main(String[] args) {
		
		AStar aStar = new AStar(4, 4, 3, 3, 0, 0, new int[][] {  {0,1}, {1,1}, {1,2}, {1,3}, {2,3}, {2,2},} );
		
		aStar.process();
		aStar.displaySolution();
		//aStar.displayPath
	}
}
