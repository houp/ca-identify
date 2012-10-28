package ga.base;

import java.util.ArrayList;
import java.util.List;

public class Population<T> {
	private List<Chromosome<T>> chromosomes;
	
	private double totalFittness = 0;
	
	public Population(List<T> elements) {
		chromosomes = new ArrayList<Chromosome<T>>(elements.size());
		for(T element : elements) {
			chromosomes.add(new Chromosome<T>(element));
		}
	}
	
	public void realculateFittnes(AbstractFittnessFunction<T> fittnes) {
		for(Chromosome<T> e : chromosomes) {
			double fit = fittnes.calculate(e.getElement());
			totalFittness += fit;
			e.setFittnes(fit);
		}
	}
	
	public int count() {
		return chromosomes.size();
	}
	
	public T getElement(T notThis) {
		int x = (int)(Math.random() * (count()-1));
		int y = (int)(Math.random() * count());
		if(x>y) {
			int t = x; x=y;y=t;
		}
		
		Chromosome<T> result = chromosomes.get(x);
		int off = 1;
		
		while(notThis != null && result.equals(notThis)) {
			result = chromosomes.get(x + off);
			off++;
		}
		
		for(int i=x+off; i<y; i++) {
			Chromosome<T> foo = chromosomes.get(i);
			if((foo.getFittnes() >= result.getFittnes()) && (!foo.equals(notThis))) {
					result = foo;
			}
		}
		
		return result.getElement();
	}
	
	public T getElement() {
		return getElement(null);
	}
	
	public Chromosome<T> best() {
		Chromosome<T> element = chromosomes.get(0);

		for(Chromosome<T> c : chromosomes) {
			double f = c.getFittnes();
			
			if(f == 1.0) return c;
			
			if(f > element.getFittnes()) {
				element = c;
			}
		}
		
		return element;
	}
	
	public double avgFittness() {
		double result = 0;
		for(Chromosome<T> c : chromosomes) {
			double f = c.getFittnes();
			result += f;
		}
		
		return result / count();
	}
	
	public double minFittness() {
		double result = Double.MAX_VALUE;
		for(Chromosome<T> c : chromosomes) {
			double f = c.getFittnes();
			if(f < result) {
				result = f;
			}
		}
		
		return result;
	}
	
	public void debug() {
		System.out.println("\n--");
		for(Chromosome<T> c : chromosomes) {
			System.out.print(String.format("%s (%f),", c.getElement(), c.getFittnes()));
		}
		System.out.println("\n--");
	}
	
}