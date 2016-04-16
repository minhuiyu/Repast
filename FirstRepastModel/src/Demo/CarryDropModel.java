package Demo;

import java.awt.Color;


import java.util.ArrayList;



import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenHistogram;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;

import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.util.SimUtilities;

/**
 * 
 * @author wisteria
 *
 */
public class CarryDropModel extends SimModelImpl{
	
    private Schedule schedule;  //ע����˽��,ʱ���ϵİ���
    
    private CarryDropSpace cdSpace;
    
    private DisplaySurface displaySurface ;
    
    private ArrayList agentList;
    
    private SimUtilities simUtilities;
    
    private OpenSequenceGraph amountOfMoneyInSpace;
    
    private OpenHistogram agentWealthDistribution;
    
    //�O��Ĭ�J����ֵ
    private static final int NumAgents=100;
    private static final int WorldXSize=40;
    private static final int WorldYSize=40;
    private static final int TotalMoney=1000;
    private static final int Agent_Min_LifeSpan=30;
    private static final int Agent_Max_LifeSpan=50;
    
    private int numAgents=NumAgents;      //�����б�
    private int worldXSize=WorldXSize;     //�^��X�S���L��
    private int worldYSize=WorldYSize;     //�^��Y�S���L��
    private int money=TotalMoney;
    private int agentMinLifespan=Agent_Min_LifeSpan;
    private int agentMaxLifespan=Agent_Max_LifeSpan;
    
    //�����ռ���Ϣ��ͼ��
    class moneyInSpace implements DataSource,Sequence{

		@Override
		public double getSValue() {
			// TODO Auto-generated method stub
			return (double)cdSpace.getTotalMoney();
		}

		@Override
		public Object execute() {
			// TODO Auto-generated method stub
			return new Double(getSValue());
		}
    	
    }
    //������״ͼ
    class agentMoney implements BinDataSource{

		@Override
		public double getBinValue(Object i) {
			// TODO Auto-generated method stub
			CarryDropAgent carryDropAgent=(CarryDropAgent)i;
			
			return (double)carryDropAgent.getMoney();
		}
    	
    }
	@Override
	public String getName() {
		// �������ط���ģ�͵����֣������ֻ�����ڷ����������ģ��Ĺ�������
		return "Carry And Drop";
	}
	
	@Override
	public void setup() {
		// ���������е�setup��ť(ѭ���ı�ʶ)���Ӧ����ʾ�ضϲ�������ָ���δ��ʼ��״̬
		System.out.println("Running setup");
		cdSpace=null;
		agentList=new ArrayList();
		schedule=new Schedule(1);    //���\�Еr����1����λ�ĕ�ͣ�g��
		if(displaySurface!=null){
			displaySurface.dispose();
		}
		displaySurface=null;
		
		//�������ͼ����̣�����ͼ�����
		if(amountOfMoneyInSpace!=null){
			amountOfMoneyInSpace.dispose();
		}
		amountOfMoneyInSpace=null;
		
		if(agentWealthDistribution!=null){
			agentWealthDistribution.dispose();
		}
		agentWealthDistribution=null;
		
        //�����µ���ʾ���̣�����עҪ��ʾ	�ı��⼰������Ϣ	
		displaySurface=new DisplaySurface(this, "Carry Drop Model Window 1");
		
		amountOfMoneyInSpace=new OpenSequenceGraph("Amount of Money In Space", this);
		 
		agentWealthDistribution=new OpenHistogram("Agent Wealth", 8, 0);
		
		registerDisplaySurface("Carry Drop Model Window 1",displaySurface);
		this.registerMediaProducer("Plot", amountOfMoneyInSpace);
	}
	
	@Override
	public void begin() {
		// ����ʼ���У���Ҫ����buildModel��buildDisplay��buildSchedule��������
		buildModel();
		buildDisplay();
		buildSchedule();
		
		displaySurface.display();
		amountOfMoneyInSpace.display();
		agentWealthDistribution.display();
	}

	private void buildSchedule() {
		// ������������з���
		System.out.println("Running BuildSchedule");
		
		//�Ȳ�����x�����Εr������
		class CarryDropStep extends BasicAction{
			
            
			@Override
			public void execute() {
				// TODO Auto-generated method stub
				simUtilities.shuffle(agentList);
				for(int i=0;i<agentList.size();i++){
					CarryDropAgent carryDropAgent=(CarryDropAgent)agentList.get(i);
					carryDropAgent.step();
				}
				
				int deadAgents=reapDeadAgents();
				for(int i=0;i<deadAgents;i++){
					addNewAgent();
				}
				
				displaySurface.updateDisplay();
				
			}
			
		}
		
		schedule.scheduleActionBeginning(0, new CarryDropStep());
		
		class CarryDropCountLiving extends BasicAction{

			@Override
			public void execute() {
				// TODO Auto-generated method stub
				countLivingAgents();
			}
			
		}
		schedule.scheduleActionAtInterval(10, new CarryDropCountLiving());
		
		class AmountOfMoneyInSpace extends BasicAction{

			@Override
			public void execute() {
				// TODO Auto-generated method stub
				amountOfMoneyInSpace.step();
			}
			
		}
		schedule.scheduleActionAtInterval(10, new AmountOfMoneyInSpace());
		
		class CarryDropUpdateAgentWealth extends BasicAction{

			@Override
			public void execute() {
				// TODO Auto-generated method stub
				agentWealthDistribution.step();
			}
			
		}
		schedule.scheduleActionAtInterval(10, new CarryDropUpdateAgentWealth());
	}

	private int reapDeadAgents() {
		// TODO Auto-generated method stub
		int count=0;
		for(int i=(agentList.size()-1);i>=0;i--){
			CarryDropAgent carryDropAgent=(CarryDropAgent)agentList.get(i);
			if(carryDropAgent.getStepsToLive()<1){
			cdSpace.removeAgentAt(carryDropAgent.getX(),carryDropAgent.getY());
			cdSpace.spreadMoney(carryDropAgent.getMoney());
			agentList.remove(i);
			count++;
			}
			
		}
		return count;
	}

	private int countLivingAgents() {
		// TODO Auto-generated method stub
		int livingAgents=0;
		for(int i=0;i<agentList.size();i++){
			CarryDropAgent carryDropAgent=(CarryDropAgent)agentList.get(i);
			if(carryDropAgent.getStepsToLive()>0) livingAgents++;
		}
		System.out.println("Number of Living agents is:"+livingAgents);
		return livingAgents;
	}

	private void buildDisplay() {
		// ������ʾ�������ݣ������ռ�������ʾ��ͳ��ͼ���
		System.out.println("Running BuildDisplay");
		ColorMap map=new ColorMap();
		for(int i=1;i<16;i++){
			map.mapColor(i,new Color((int)(i*8+127),0,0));
			
	}
		map.mapColor(0, Color.white);
		Value2DDisplay displayMoney=
				new Value2DDisplay(cdSpace.getCurrentMoneySpace(), map);
		
		
		Object2DDisplay displayAgents=
				new Object2DDisplay(cdSpace.getCurrentAgentSpace());
		displayAgents.setObjectList(agentList);
		
		displaySurface.addDisplayableProbeable(displayMoney,"Money");
		displaySurface.addDisplayableProbeable(displayAgents, "Agents");
	}

	private void buildModel() {
		// ����ģ���е���������󡢿ռ�����
		System.out.println("Running BuildModel");
		cdSpace=new CarryDropSpace(worldXSize, worldYSize);
		cdSpace.spreadMoney(money);
		
		for(int i=0;i<numAgents;i++){
			addNewAgent();
		}
		for(int i=0;i<agentList.size();i++){
			CarryDropAgent carryDropAgent=(CarryDropAgent)agentList.get(i);
			carryDropAgent.report();
		}
	}

	private void addNewAgent() {
		// TODO Auto-generated method stub
		CarryDropAgent agent=new CarryDropAgent(agentMinLifespan, agentMaxLifespan);
		agentList.add(agent);
		cdSpace.addAgent(agent);
	}

	@Override
	public Schedule getSchedule() {
		/*getScheduale�������뷵��һ��Schedule���͵Ķ���
		����ÿ��Repastģ�Ͷ�����Ҫ��һ��Schedule����.
		*/
		return schedule;
	}
	
	@Override
	public String[] getInitParam() {
		/* ����һ���ַ����������飬�������г���һ��������������֡�
		 * ��Repast������������ǿ���������Щ������
		 */
		String[] initParams={"NumAgents","WorldXSize","WorldYSize","Money",
				"AgentMinLifespan","AgentMaxLifespan"};
		return initParams;
	}
	
	public int getNumAgents() {
		return numAgents;
	}
	public void setNumAgents(int numAgents) {
		this.numAgents = numAgents;
	}

	
	public int getWorldXSize() {
		return worldXSize;
	}
	public void setWorldXSize(int worldXSize) {
		this.worldXSize = worldXSize;
	}
	public int getWorldYSize() {
		return worldYSize;
	}
	public void setWorldYSize(int worldYSize) {
		this.worldYSize = worldYSize;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
//	public DisplaySurface getDisplaySurface() {
//		return displaySurface;
//	}
//	public void setDisplaySurface(DisplaySurface displaySurface) {
//		this.displaySurface = displaySurface;
//	}
	
	public int getAgentMinLifespan() {
		return agentMinLifespan;
	}

	public void setAgentMinLifespan(int agentMinLifespan) {
		this.agentMinLifespan = agentMinLifespan;
	}

	public int getAgentMaxLifespan() {
		return agentMaxLifespan;
	}

	public void setAgentMaxLifespan(int agentMaxLifespan) {
		this.agentMaxLifespan = agentMaxLifespan;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimInit init=new SimInit();  //����һ��SimInit��͵Č���
		CarryDropModel model=new CarryDropModel();  //������ģ�͌���
		init.loadModel(model, "", false); //��Init�����e��loadModel�����b�dģ�ͣ�LoadModel()�����ą������x?
         
	}
}
