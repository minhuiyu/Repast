package Demo;

import org.apache.bcel.generic.NEW;

import uchicago.src.sim.space.Discrete2DSpace;
import uchicago.src.sim.space.Object2DGrid;

public class CarryDropSpace {
	private Object2DGrid moneySpace;
	
	private Object2DGrid agentSpace;
	
	public CarryDropSpace(int xSize,int ySize){
		moneySpace=new Object2DGrid(xSize, ySize);
		agentSpace=new Object2DGrid(xSize, ySize);
		for(int i=0;i<xSize;i++){
			for(int j=0;j<ySize;j++){
				moneySpace.putObjectAt(i, j, new Integer(0));//����׌���оW���ֵ����0�������]���X
			
			}
		}
	}

	public void spreadMoney(int money) {
		// �S�C�x��һ�����X
		for(int i=0;i<money;i++){
			int x=(int)(Math.random())*(moneySpace.getSizeX());
			int y=(int)(Math.random())*(moneySpace.getSizeY());
//			int I;
//			if(moneySpace.getObjectAt(x, y)!=null){
//				I=((Integer)moneySpace.getObjectAt(x, y)).intValue();
//			}else{
//				I=0;
//			}
			int currentValue=getMoneyAt(x,y);
			moneySpace.putObjectAt(x, y, new Integer(currentValue+1));
		}
	}

	private int getMoneyAt(int x, int y) {
		// TODO Auto-generated method stub
		int i;
		if(moneySpace.getObjectAt(x, y)!=null){
			i=((Integer)moneySpace.getObjectAt(x, y)).intValue();
		}else{
			i=0;
		}
		return i;
	}

	public Object2DGrid getCurrentMoneySpace() {
		// TODO Auto-generated method stub
		return moneySpace;
	}
   
	public boolean isCellOccupied(int x,int y){
		boolean retVal=false;
		if(agentSpace.getObjectAt(x, y)!=null) retVal=true;
			return retVal;
		
	}
	
	public boolean addAgent(CarryDropAgent agent){
		boolean retVal=false;
		int count=0;
		int countLimit=10*agentSpace.getSizeX()*agentSpace.getSizeY();
		/*����10��ԭ���ǣ����W��ȫ���M�ˣ��]�пվW��r��ԓ��������countLimit�Σ�
		�����countlimit��֮����Ȼ�]�У��t�ŷ���false�����򽡉��Ժ�һ�c��
		*/
		while((retVal==false)&&(count<countLimit)){
			int x=(int)(Math.random()*(agentSpace.getSizeX()));
			int y=(int)(Math.random()*(agentSpace.getSizeY()));
			if(isCellOccupied(x, y)==false){
				//��ʾ�ĸ�]�б�����
				agentSpace.putObjectAt(x, y, agent);
				agent.setXY(x, y);
				agent.setCarryDropSpace(this);
				retVal=true;
			}
			count++;
			
		}
			return retVal;	
	}

	public Discrete2DSpace getCurrentAgentSpace() {
		// TODO Auto-generated method stub
		return agentSpace;
	}
	
	public void removeAgentAt(int x,int y){
		agentSpace.putObjectAt(x, y, null);
		
	}

	public int takeMoneyAt(int x, int y) {
		// TODO Auto-generated method stub
		int money=getMoneyAt(x, y);
		moneySpace.putValueAt(x, y, new Integer(0)); //agent ����Ǯ�󣬸���Ǯ������Ϊ0
		return money;
	}

	public boolean moveAgentAt(int x, int y, int newX, int newY) {
		// TODO Auto-generated method stub
		boolean retVal=false;
		if(!isCellOccupied(newX, newY)){
			CarryDropAgent carryDropAgent=(CarryDropAgent)agentSpace.getObjectAt(x, y);
			removeAgentAt(x, y);
			agentSpace.putObjectAt(newX, newY, carryDropAgent);
			retVal=true;
		}
		return retVal;
	}

	public CarryDropAgent getAgentAt(int newX, int newY) {
		// TODO Auto-generated method stub
		CarryDropAgent retValAgent=null;
		if(agentSpace.getObjectAt(newX, newY)!=null){
			retValAgent=(CarryDropAgent)agentSpace.getObjectAt(newX, newY);
		}
		return retValAgent;
	}

	public double getTotalMoney() {
		// TODO Auto-generated method stub
		int totalMoney=0;
		for(int i=0;i<agentSpace.getSizeX();i++){
			for(int j=0;j<agentSpace.getSizeY();j++){
				totalMoney +=getMoneyAt(i, j);
			}
		}
		return totalMoney;
	}

//	public int getX() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getY() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public int getMoney() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
}
