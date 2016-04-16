package Demo;

import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

public class CarryDropAgent implements Drawable{
	private int x;
	private int y;
	private int vX;  //agent�ƶ����ٶ�
	private int vY;
	private int money;
	private int stepsToLive;  //�x����߀ʣ���ٲ�
	private static int IdNumber=0;
	private int ID;    //��ÿ�������ṩһ��ΨһID׃��
	
	private CarryDropSpace carryDropSpace;    //��agent֪���Լ����ڵĿռ�
	
	public void setCarryDropSpace(CarryDropSpace cDropSpace){
		carryDropSpace=cDropSpace;
	}
	public CarryDropAgent(int minLifespan,int maxLifespan){
		x=-1;
		y=-1;
		setMoney(0);
		setVxVy();
		setStepsToLive((int)(Math.random()*(maxLifespan-minLifespan)+minLifespan));
		IdNumber++;
		setID(IdNumber);
	}
	
	private void setVxVy() {
		// TODO Auto-generated method stub
		vX=0;
		vY=0;
		while((vX==0)&&(vY==0)){
			vX=(int)Math.floor(Math.random()*3)-1;
			vY=(int)Math.floor(Math.random()*3)-1;
		}
	}
	public void setXY(int newX,int newY){
		x=newX;
		y=newY;
	}

	public String getID() {
		return "Agent-"+ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getStepsToLive() {
		return stepsToLive;
	}

	public void setStepsToLive(int stepsToLive) {
		this.stepsToLive = stepsToLive;
	}
	
	//report()���ã���ָ�������г�����׃����ֵ
	public void report(){
		System.out.println(getID()+"at"+x+","+y+"has"+getMoney()+
				"dollors and"+getStepsToLive()+"steps to live.");
	}

	@Override
	public void draw(SimGraphics simGraphics) {
		// TODO Auto-generated method stub
		if(stepsToLive>10){
			simGraphics.drawFastRoundRect(Color.green);
		}else{
			simGraphics.drawFastRoundRect(Color.blue);
		}
		
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}

	public void step() {
		// TODO Auto-generated method stub
		int newX=x+vX;
		int newY=x+vY;
		
		Object2DGrid grid=(Object2DGrid) carryDropSpace.getCurrentAgentSpace();
		newX=grid.getSizeX()%grid.getSizeX();
		newY=grid.getSizeY()%grid.getSizeY();
		
		if(tryMove(newX,newY)){
			money +=carryDropSpace.takeMoneyAt(x,y); //agent��Ǯ
		}else{
			CarryDropAgent carryDropAgent=carryDropSpace.getAgentAt(newX,newY);
			if(carryDropAgent!=null){
				if(money>0){
					carryDropAgent.receiveMoney(1);
					money--;
				}
			}
			setVxVy();
		}
		
		stepsToLive--;
	}
	
	private void receiveMoney(int i) {
		// TODO Auto-generated method stub
		money +=i;
	}
	private boolean tryMove(int newX, int newY) {
		// TODO Auto-generated method stub
		
		return carryDropSpace.moveAgentAt(x,y,newX,newY);
	}
}
