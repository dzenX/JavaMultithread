import java.io.FileNotFoundException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class sync 
{
	public static CyclicBarrier CB1 = new CyclicBarrier(2);
	public static CyclicBarrier CB2 = new CyclicBarrier(2);	
	public static Semaphore S1 = new Semaphore(0, true);
	public static Semaphore S2 = new Semaphore(0, true);
}

class buf
{
	//ints for goof
	private static final int sz = 100;
	private static int[] goof = new int[sz];
	private static int poped = 0;
	private static int pushed = 0;
	
	//monitor
	private static ReentrantLock RL = new ReentrantLock(); 
	private static Condition n_empty = RL.newCondition();
	private static Condition n_full = RL.newCondition();
	
	public static void insert(int sho, int kto)
	{
		RL.lock();
		while (pushed - poped == sz)
		{
			try 
			{
				n_full.await();
			}
			catch (InterruptedException exc)
			{
				exc.printStackTrace();
			}
		}
		goof[pushed] = sho;
		System.out.println("Imperial shooter X256674" + kto + " missed " + sho + " times in room @" + pushed);
		pushed = (pushed + 1) % sz;
		n_empty.signal();
		RL.unlock();
	}
	
	public static int take(int kto)
	{
		RL.lock();
		while(pushed - poped == 0)
		{
			try
			{
				n_empty.await();
			}
			catch (InterruptedException exc)
			{
				exc.printStackTrace();
			}
		}
		int out = goof[poped];
		poped = (poped + 1) % sz;
		n_full.signal();
		RL.unlock();
		return out;
	}
}

class common
{
	public static boolean bool = false;
	public static int number = 0;
	public static char ch = 'a';
	public static byte bt = 0;
	public static short sh = 0;
	public static long lg = 0;
	public static float fl = 0;
	public static double db = 0;
	public static ReentrantLock RL = new ReentrantLock();
	public static void print(int thread_id)
	{
		RL.lock();
		System.out.println("Thread "+ thread_id + "; bool = " + bool);
		System.out.println("Thread " + thread_id + "; number = " + number);
		//System.out.println("Thread " + thread_id + "; char = " + ch);
		System.out.println("Thread " + thread_id + "; byte = " + bt);
		System.out.println("Thread " + thread_id + "; long = " + lg);
		System.out.println("Thread " + thread_id + "; float = " + fl);
		RL.unlock();
	}
}

class Producer implements Runnable
{
	Thread t;
	int id;
	Producer(int thread_id)
	{
		id = thread_id;
		t = new Thread(this, "Imperial shooter X256674" + thread_id);
		t.start();
	}
	public void run() 
	{
		while(true)
		{
			try
			{
				buf.insert(0, id);
				if (lb.magic)
					Thread.sleep(1);
				if (id == 2 || id == 3)
				{
					try {
						System.out.println("Imperial shooter X256674" + id +" tried to see the force in CB1");
						sync.CB1.await();
						System.out.println("Jedi showed the force in CB1 for the imperial shooter X256674" + id + " by his laser sword");
					}
					catch(BrokenBarrierException e)
					{
						e.printStackTrace();
					}
				}
				if (id == 4 || id == 5)
				{
					try {
						System.out.println("Imperial shooter X256674" + id +" tried to see the force in CB2");
						sync.CB2.await();
						System.out.println("Jedi showed the force  in CB2 for the imperial shooter X256674" + id + " by his laser sword");
					}
					catch(BrokenBarrierException e)
					{
						e.printStackTrace();
					}
				}
				if (id == 3)
				{
					sync.S1.release();
					sync.S2.acquire();
				}
				if (id == 2)
				{
					common.RL.lock();
					common.bool = !common.bool;
					common.RL.unlock();
				}
				if (id == 3)
				{
					common.RL.lock();
					common.bt++;
					common.RL.unlock();
				}
				if (id == 4)
				{
					common.RL.lock();
					common.number++;
					common.RL.unlock();
				}
				if (id == 5)
				{
					common.RL.lock();
					common.fl++;
					common.RL.unlock();
				}
				common.print(id);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}

class Consumer implements Runnable
{
	int id;
	Thread t;
	Consumer(int thread_id)
	{
		id = thread_id;
		t = new Thread(this, "Han Solo from episode " + thread_id);
		t.start();
	}
	public void run(){
		while(true)
		{
			try
			{
				buf.take(id);
				if(lb.magic)
					Thread.sleep(1);
				if (id == 1)
				{
					sync.S2.release();
					sync.S1.acquire();
				}
				if(id == 1)
				{
					common.RL.lock();
					common.lg++;
					common.RL.unlock();
				}
				common.print(id);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
public class lb
{
	static boolean magic = true;
	public static void main(String[] args) throws FileNotFoundException
	{
		System.out.println("VIVA REVOLUTION !!!");
		System.out.flush();
		Consumer P1 = new Consumer(1);
		Producer P2 = new Producer(2);
		Producer P3 = new Producer(3);
		Producer P4 = new Producer(4);
		Producer P5 = new Producer(5);
		try 
		{
			P1.t.join();
			P2.t.join();
			P3.t.join();
			P4.t.join();
			P5.t.join();
		}
		catch(InterruptedException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
