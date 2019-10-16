import java.util.Observable;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

interface Observer
{	void update(Observable t, Object o);
}

public class MVCTempConvert
{	public static void main(String args[])
	{	TemperatureModel temperature = new TemperatureModel();
		new FarenheitGUI(temperature, 100, 100);
		new CelsiusGUI(temperature,100, 250);
		new GraphGUI(temperature, 200, 500);
	}
}


abstract class TemperatureGUI implements java.util.Observer
{	TemperatureGUI(String label, TemperatureModel model, int h, int v)
	{	this.label = label;
		this.model = model;
		temperatureFrame = new Frame(label);
		temperatureFrame.add("North", new Label(label));
		temperatureFrame.add("Center", display);
		Panel buttons = new Panel();
		buttons.add(upButton);
		buttons.add(downButton);
		temperatureFrame.add("South", buttons);
		temperatureFrame.addWindowListener(new CloseListener());
		model.addObserver(this); // Connect the View to the Model
		temperatureFrame.setSize(200,100);
		temperatureFrame.setLocation(h, v);
		temperatureFrame.setVisible(true);
	}

	public void setDisplay(String s){ display.setText(s);}

	public double getDisplay()
	{	double result = 0.0;
		try
		{	result = Double.valueOf(display.getText()).doubleValue();
		}
		catch (NumberFormatException e){}
		return result;
	}

	public void addDisplayListener(ActionListener a){ display.addActionListener(a);}
	public void addUpListener(ActionListener a){ upButton.addActionListener(a);}
	public void addDownListener(ActionListener a){ downButton.addActionListener(a);}

	protected TemperatureModel model(){return model;}

	private String label;
	private TemperatureModel model;
	private Frame temperatureFrame;
	private TextField display = new TextField();
	private Button upButton = new Button("Raise");
	private Button downButton = new Button("Lower");

	public static class CloseListener extends WindowAdapter
	{	public void windowClosing(WindowEvent e)
		{	e.getWindow().setVisible(false);
			System.exit(0);
		}
	}
}

class FarenheitGUI extends TemperatureGUI
{	public FarenheitGUI(TemperatureModel model, int h, int v)
	{	super("Farenheit Temperature", model, h, v);
		setDisplay(""+model.getF());
		addUpListener(new UpListener());
		addDownListener(new DownListener());
		addDisplayListener(new DisplayListener());
	}

	public void update(Observable t, Object o) // Called from the Model
	{	setDisplay("" + model().getF());
	}

	class UpListener implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	model().setF(model().getF() + 1.0);
		}
	}

	class DownListener implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	model().setF(model().getF() - 1.0);
		}
	}

	class DisplayListener implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	double value = getDisplay();
			model().setF(value);
		}
	}
}
//celsius
class CelsiusGUI extends TemperatureGUI
{	public CelsiusGUI(TemperatureModel model, int h, int v)
	{	super("Celsius Temperature", model, h, v);
		setDisplay(""+model.getF());
		addUpListener(new UpListener());
		addDownListener(new DownListener());
		addDisplayListener(new DisplayListener());
	}

	public void update(Observable t, Object o) // Called from the Model
	{	setDisplay("" + model().getC());
	}

	class UpListener implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	model().setC(model().getC() + 1.0);
		}
	}

	class DownListener implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	model().setC(model().getC() - 1.0);
		}
	}

	class DisplayListener implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	double value = getDisplay();
			model().setC(value);
		}
	}
}

class TemperatureCanvas extends Canvas
{	public TemperatureCanvas(TemperatureGauge farenheit)
	{	_farenheit = farenheit;
	}

	public void paint(Graphics g)
	{	g.setColor(Color.black);
		g.drawRect(left,top, width, height);
		g.setColor(Color.red);
		g.fillOval(left-width/2, top+height-width/3,width*2, width*2);
		g.setColor(Color.black);
		g.drawOval(left-width/2, top+height-width/3,width*2, width*2);
		g.setColor(Color.white);
		g.fillRect(left+1,top+1, width-1, height-1);
		g.setColor(Color.red);
		long redtop = height*(_farenheit.get()-_farenheit.getMax())/(_farenheit.getMin()-_farenheit.getMax());
		g.fillRect(left+1, top + (int)redtop, width-1, height-(int)redtop);
	}

	private TemperatureGauge _farenheit;
	private static final int width = 20;
	private static final int top = 20;
	private static final int left = 100;
	private static final int right = 250;
	private static final int height = 200;
}

class TemperatureGauge
{	public TemperatureGauge(int min, int max){ Min = min; Max = max; }

	public void set(int level) { current = level; }
	public int get(){return current;}
	public int getMax(){return Max;}
	public int getMin(){return Min;}

	private int Max, Min, current;
}






interface ValueStrategy
{	public void set(double d);
	public double get();
}
class CelsiusStrategy implements ValueStrategy
{	public CelsiusStrategy(TemperatureModel m) {model = m;}
	public void set(double d){model.setC(d);}
	public double get(){ return model.getC();}

	private TemperatureModel model;
}