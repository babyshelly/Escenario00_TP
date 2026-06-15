import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Mundo01 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Mundo01 extends MundoBase
{

    public Mundo01() {
        super(10, 8);
    }

    protected void generarNaves() {
        agregar(new NaveExploradoraEnemiga(Direccion.NORTE), 4, 2);
        agregar(new NaveDeAtaqueEnemiga(Direccion.NORTE), 6, 4);

        NaveDeAtaque nave = new NaveDeAtaque();
        agregar(nave, 0, 7);
        PilotoRescate piloto = new PilotoRescate();
        agregar(piloto, 1, 7);
        piloto.subirse(nave);
    }

    protected void generarPOIs() {
        marcarCelda(0, 0, new Color(0, 0, 200, 150));
        marcarCelda(9, 0, new Color(200, 0, 0, 150));
    }

    protected void generarItems() {
        agregar(new ItemDeCombustible(), 5, 4);
        agregar(new ItemDeCombustible(), 2, 3);
        agregar(new ItemDeCombustible(), 8, 2);
        agregar(new ContenedorDeRecursos(400, "MAT-001"), 3, 1);
        agregar(new ContenedorDeRecursos(600, "MAT-002"), 7, 3);
        agregar(new ContenedorDeRecursos(250, "OMEGA-003"), 1, 5);
        agregar(new ContenedorDeRecursos(900, "CRIS-001"), 9, 0);
        agregar(new ContenedorDeRecursos(150, "CHATARRA-01"), 4, 4);
    }

    protected void generarAsteroides() {
        agregar(new Asteroide(), 2, 0);
        agregar(new Asteroide(), 5, 2);
        agregar(new AsteroideDeMineral(), 8, 5);
    }
}
