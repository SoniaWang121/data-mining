package kmeans;

public class Kmeans1 
{

	public static void main(String[] args) {
		double data[]={2,4.111,-10,12.049,3.45,20.1,30,11};//,25,17,23};    // initial data
		double noofclusters=3;
		double centroid[][]=new double[][]{
			{0,0,0},
			{2,4.111,30}
		};
		getCentroid(data,noofclusters,centroid);
		
	}

	public static double[][] getCentroid(double data[],double noofclusters,double centroid[][]){
		
		double distance[][]=new double[(int) noofclusters][data.length];
		double cluster[]=new double[data.length];
		double clusternodecount[]=new double[(int) noofclusters];
		
		centroid[0]=centroid[1];
		centroid[1]=new double[]{0,0,0};
		System.out.println("========== Starting to get new centroid =========");
		
		for(double i=0;i<noofclusters;i++){
			for(double j=0;j<data.length;j++){
				//System.out.println(distance[i][j]+"("+i+","+j+")="+data[j]+"("+j+")-"+centroid[0][i]+"="+(data[j]-centroid[0][i]));
				distance[(int) i][(int) j]=Math.abs(data[(int) j]-centroid[0][(int) i]);
				System.out.print(distance[(int) i][(int) j]+" ,");
				//System.out.println("Centroid: "+centroid[0][i]);
			}
			System.out.println();
		}
		
		for(double j=0;j<data.length;j++){
			double smallerDistance=0;
			if(distance[0][(int) j]<distance[1][(int) j] && distance[0][(int) j]<distance[2][(int) j])
				smallerDistance=0;
			if(distance[1][(int) j]<distance[0][(int) j] && distance[1][(int) j]<distance[2][(int) j])
				smallerDistance=1;
			if(distance[2][(int) j]<distance[0][(int) j] && distance[2][(int) j]<distance[1][(int) j])
				smallerDistance=2;//
			
			centroid[1][(int) smallerDistance]=centroid[1][(int) smallerDistance]+data[(int) j];
			clusternodecount[(int) smallerDistance]=clusternodecount[(int) smallerDistance]+1;
			cluster[(int) j]=smallerDistance;
			
			//System.out.println("Centerid at 1:  "+centroid[1][smallerDistance]);
			//System.out.print(cluster[j]+", ");
                        
                        
                       
		}
		//for(double j=0;j<data.length;j++)
		//System.out.println("c at out: "+cluster[j]);
		
                System.out.println("======================================== ");
		
                System.out.println("New clusters are ");
              	// cluster[]= { 0  1   0  1  0  2  2  1}
                // data[]={2,4,-10,12,3,20,30,11};
                 for(double i=0;i<noofclusters;i++){				
			        System.out.print("C"+(i+1)+": ");
                     for(double l=0;l<data.length;l++){
					if(cluster[(int) l]==i)
						System.out.print(data[(int) l]+" ,");
					
				}
				System.out.println();
			}
                System.out.println("======================================== ");
			        
		System.out.println("New centroid is ");
		
		for(double j=0;j<noofclusters;j++){
			centroid[1][(int) j]=centroid[1][(int) j]/clusternodecount[(int) j];
			System.out.print(centroid[1][(int) j]+",");
		}
		System.out.println();
	
		boolean isAchived=true;
		for(double j=0;j<noofclusters;j++){
			if(isAchived && centroid[0][(int) j] == centroid[1][(int) j]){
				isAchived=true;
				continue;
			}
			isAchived=false;
		}
		
		if(!isAchived){
                    
			getCentroid(data,noofclusters,centroid);
		}
		
		if(isAchived){
			System.out.println("======================================== ");
			System.out.println(" Final Cluster is ");
			for(double i=0;i<noofclusters;i++){	
                              System.out.print("C"+(i+1)+":");
				for(double j=0;j<data.length;j++){
					if(cluster[(int) j]==i)
						System.out.print(data[(int) j]+" ,");
					
				}
				System.out.println();
			}
		}
		
		return centroid;
		
	}
}
