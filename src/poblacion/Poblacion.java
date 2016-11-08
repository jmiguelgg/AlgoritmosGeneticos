/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poblacion;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hatake
 */
public class Poblacion {
	private ArrayList<Individuo> individuos;
    private ArrayList<Integer> puntoDeCruza;

    public Poblacion(Integer alelos,Integer individuos){
    	this.individuos = new ArrayList<Individuo>();
    	puntoDeCruza = new ArrayList<Integer>();
    	generarPolacion(alelos,individuos);
    }

    public Poblacion(ArrayList<Individuo> in, ArrayList<Integer> pdc){
    	individuos = in;
    	puntoDeCruza = pdc;
    }

    public Integer getIndividuos(){
    	return individuos.size();
    }

    public Individuo getIndividuo(Integer in){
    	return individuos.get(in);
    }

    public ArrayList<Integer> getPuntoDeCruza(){
    	return puntoDeCruza;
    }

    public Integer getPuntoDeCruzaPorIndividuo(Integer pCruza){
        return puntoDeCruza.get(pCruza);
    }

    private void generarPolacion(Integer alelos,Integer individuos){
    	for (int i = 0; i < individuos ; i++) {
    		this.individuos.add(new Individuo(alelos));
    	}
    }

    public double probabilidad(Integer individuo){
    	return individuos.get(individuo).aptitud()*1.0/aptitud()*1.0;
    }

    public Double aptitud() {
        Double sumAptitud;
        sumAptitud = 0.0;
        for(Individuo in: individuos)
            sumAptitud += in.aptitud();
        return sumAptitud;
    }

    public Double valor(){
    	Double sumValor;
        sumValor = 0.0;
        for(Individuo in: individuos)
            sumValor += in.valor();
        return sumValor;
    }

    public Double promedioAptitud(){
    	return Double.valueOf( aptitud()/getIndividuos() );
    }

    public Double promedioValor(){
    	return Double.valueOf( valor()/getIndividuos() );
    }

    public Double maxAptitud(){
		Double tmpIndi,aux;
		aux = 0.0;
		for (int i = 0; i < getIndividuos(); i++){
			tmpIndi = individuos.get(i).aptitud();
			if (tmpIndi > aux)
				aux = tmpIndi;
		}
		return aux;
    }

    /**
     * Regresa los datos para graficar la probabilidad de cada indivuduo de la poblacion
     */
    public ArrayList<Double> datosPorPoblacion(){
    	ArrayList<Double> datos = new ArrayList<Double>();
    	Integer aux = 0;
    	for (Individuo in: individuos)
    		datos.add(probabilidad(aux++));
    	return datos;
    }

    /**
     * Primera mutación simple, se intercambian los cromozomas de dos 
     * individuos dependiendo del punto de cruza aleatorio
     */
    public Poblacion cruza1() throws CloneNotSupportedException{
    	Random rn = new Random();
    	ArrayList<Individuo> auxIND = new ArrayList<Individuo>();
        ArrayList<Integer> pCruza = new ArrayList<Integer>();
        Individuo inaux,inaux2;
    	Integer in,al,pc;
    	in = getIndividuos();
    	al = individuos.get(0).getAlelos();
    	for (int i = 0; i < in; i += 2){
            pc = rn.nextInt(al);
            pCruza.add(pc);
            pCruza.add(pc);
            inaux = new Individuo((ArrayList<Alelo>) individuos.get(i).clone());
            inaux2 = new Individuo((ArrayList<Alelo>) individuos.get(i+1).clone());
            
            for (int j = al-1; j >= pc; j--){
                inaux.reamplazarAlelo(j,individuos.get(i+1).get(j).getValor());
                inaux2.reamplazarAlelo(j,individuos.get(i).get(j).getValor());
            }
            auxIND.add(inaux);
            auxIND.add(inaux2);
	}
        return new Poblacion(auxIND, pCruza);
    }

    /**
     * La mutación se hace a un alelo de todos los individuos aleatoriamente
     * @return 
     */
    public Poblacion muta1(Double porcentaje) throws CloneNotSupportedException{
    	Random rn = new Random();
    	ArrayList<Individuo> auxIND = new ArrayList<Individuo>();
        ArrayList<Integer> cambios = individuosAMutar(porcentaje);
    	Integer tmp,al,in,aux,contador;
        Individuo auxIndi;
    	al = individuos.get(0).getAlelos();
        in = individuos.size();
        contador = 0;
    	for (int x = 0; x < in;x++) {
            if(cambios.get(contador) == x){
                tmp = rn.nextInt(al);
                auxIndi = new Individuo(individuos.get(x));
                auxIndi.get(tmp).cambiarValor();
                auxIND.add(auxIndi);
            }
            else{
                auxIndi = new Individuo(individuos.get(x));
                auxIND.add(auxIndi);
            }
    	}
    	return new Poblacion(auxIND,getPuntoDeCruza());
    }
    
    private ArrayList<Integer> individuosAMutar(Double porcentaje){
        ArrayList<Integer> seleccion = new ArrayList<Integer>();
        Random rn = new Random();
        Integer cantidad =0;
        Double aux = porcentaje*individuos.size()/100;
        cantidad = aux.intValue();
        for(int x = 0;x<cantidad;x++){
            seleccion.add(rn.nextInt(individuos.size()));
        }
        return seleccion;
    }
    
    public void imprimirPoblacion(){
    	Integer aux = 1;
        for(Individuo in: individuos){
        	System.out.print(aux++ +" : " + in);
        }
    }
    
    private Double ruletaDouble(){
        Random rn = new Random();
        return rn.nextInt(100)*1.0/100.0;
    }
    
    private Integer ruletaInteger(){
        Random rn = new Random();
        return rn.nextInt(100);
    }
    
    private Individuo acumulado(Double ac){
        Double cumulo = 0.0;
        Integer tmp = 0;
        for(int x = 0;x<individuos.size();x++){
            if(ac >= cumulo)
                cumulo += probabilidad(x);
            else{
                tmp = x;
                break;
            }
        }
        return individuos.get(tmp);
    }
    
    private Individuo torneo(Double torneo){
        Random rn = new Random();
        Integer rn1,rn2;
        rn1 = rn.nextInt(individuos.size());
        rn2 = rn.nextInt(individuos.size());
        return competir(individuos.get(rn1),individuos.get(rn2),torneo);
    }
    
    private Individuo competir(Individuo in1, Individuo in2,Double torneo){
        Double aux1,aux2;
        Integer mayor,menor;
        aux1 = in1.aptitud();
        aux2 = in2.aptitud();
        if (aux1 > aux2){
            mayor = 1;
            menor = 2;
        }
        else{
            mayor = 2;
            menor = 1;
        }
        
        if(ganador(torneo,mayor,menor) == 1)
            return in1;
        else
            return in2;
    }
    
    private Integer ganador(Double torneo,Integer mayor,Integer menor){
        if(ruletaDouble() <= torneo)
            return mayor;
        else
            return menor;
    }
    
    public void seleccionarPorRuleta(){
        ArrayList<Individuo> newPoblacion =  new ArrayList<Individuo>();
        newPoblacion.add(maxAlelo());
        for(int x = 0;x < individuos.size()-1;x++){
            newPoblacion.add(acumulado(ruletaDouble()));
        }
        individuos = newPoblacion;
    }
    
    public void seleccionarPorTorneoP(Double torneo){
        ArrayList<Individuo> newPoblacion = new ArrayList<Individuo>();
        for(int x = 0; x < individuos.size();x++){
            newPoblacion.add(torneo(torneo));
        }
        individuos = newPoblacion;
    }
    
    public Individuo maxAlelo(){
        Double aux = 0.0;
        Individuo in = null;
        for(int x = 0;x < individuos.size();x++){
            if(individuos.get(x).aptitud() > aux){
                aux = individuos.get(x).aptitud();
                in = individuos.get(x);
            }
        }
        return in;
    }
    
    public Double min(){
        Double aux = max();
        for(int x = 0;x < individuos.size();x++){
            if(individuos.get(x).aptitud() < aux)
                aux = individuos.get(x).aptitud();
        }
        return aux;
    }
    
    public Double max(){
        Double aux = 0.0;
        for(int x = 0;x < individuos.size();x++){
            if(individuos.get(x).aptitud() > aux)
                aux = individuos.get(x).aptitud();
        }
        return aux;
    }

    @Override
    protected void finalize() throws Throwable {
    	individuos.clear();
    	puntoDeCruza.clear();
    }
    
    @Override
    public String toString(){
    	return "La poblacion tiene las caracteristicas:\n\tAlelos: " + individuos.get(0) + "\n\tIndividuos: " + this.getIndividuos();
    }
}
