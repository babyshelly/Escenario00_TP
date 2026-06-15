import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ContenedorDeRecursos here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ContenedorDeRecursos extends Item {

    private int creditos;
    private String codigoManifiesto;

    public ContenedorDeRecursos(int creditos, String codigoManifiesto) {
        this.creditos = creditos;
        this.codigoManifiesto = codigoManifiesto;
    }

    public int getCreditos() {
        return this.creditos;
    }

    public String getCodigoManifiesto() {
        return this.codigoManifiesto;
    }

    @Override
    public int serRecogido() {
        getWorld().removeObject(this);
        return this.creditos;
    }
}
