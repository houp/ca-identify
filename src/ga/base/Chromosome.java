package ga.base;

public class Chromosome<T> implements Comparable<Chromosome<T>> {
	private T element;
	private double fittnes;
	
	public Chromosome(T element) {
		this.element = element;
	}

	public T getElement() {
		return element;
	}

	public void setElement(T element) {
		this.element = element;
	}

	public double getFittnes() {
		return fittnes;
	}

	public void setFittnes(double fittnes) {
		this.fittnes = fittnes;
	}

	@Override
	public int compareTo(Chromosome<T> o) {
		return Double.compare(fittnes, o.getFittnes());
	}
}