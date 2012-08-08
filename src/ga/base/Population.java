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
	
//	public T getElement(T notThis) {
//		if(chromosomes == null || chromosomes.size() == 0) {
//			return null;
//		}
//		
//		double r = Math.random() * totalFittness;
//		double sum = 0;
//		for(Chromosome<T> c : chromosomes) {
//			sum += c.getFittnes();
//			if(sum >= r && (notThis == null || c != notThis)) {
//				return c.getElement();
//			}
//		}
//		
//		return chromosomes.get(chromosomes.size() - 1).getElement();
//	}
	
	public T getElement(T notThis) {
		int x = (int)(Math.random() * count());
		int y = (int)(Math.random() * count());
		
		if(x==y) return chromosomes.get(x).getElement();
		
		if(x>y) {
			int t = y;
			y = x;
			x = t;
		}
		
		Chromosome<T> result = chromosomes.get(x + (int)(Math.random()*(y-x)));
		
		while(notThis != null && result.equals(notThis)) {
			result = chromosomes.get(x + (int)(Math.random()*(y-x)));
		}
		
		for(int i=x;i<y;i++) {
			Chromosome<T> foo = chromosomes.get(i);
			if(foo.getFittnes() > result.getFittnes() && !foo.equals(notThis))
				if(Math.random() > 0.55) {
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
		
		return result / (double)count();
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
}