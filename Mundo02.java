import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Mundo02 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Mundo02 extends MundoBase
{

    public Mundo02() {
        super(10, 8);
    }

    protected void generarNaves() {
        agregar(new NaveExploradoraEnemiga(Direccion.OESTE), 4, 3);
        agregar(new NaveDeAtaqueEnemiga(Direccion.NORTE), 6, 5);

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
        agregar(new ItemDeCombustible(), 4, 7);
        agregar(new ItemDeCombustible(), 9, 4);
        agregar(new ItemDeCombustible(), 5, 2);  
        agregar(new ItemDeCombustible(), 1, 3);
        agregar(new ItemDeCombustible(), 7, 1);
        agregar(new ItemDeCombustible(), 2, 1);
        agregar(new ContenedorDeRecursos(1000, "OMEGA-MAX"), 5, 0);
        agregar(new ContenedorDeRecursos(750, "CRIS-OMEGA-X"), 9, 7);
        agregar(new ContenedorDeRecursos(300, "MAT-099"), 0, 3);
    }

    protected void generarAsteroides() {
        boolean[][] mapa = {
            {false, false, true,  false, false, false, true,  false, false, false},
            {false, true,  false, false, true,  false, false,  false,  true, false},
            {false, false, false, true,  false, false, false, false, false,  false},
            {false, false, true,  false, false, true,  false, false,   true, false},
            {false, true,  false, false, false, false, false, false,  true, false},
            {false, false, false, false, false, false, false,  false, false, false },  // ← D6 y F6 libres
            {false, false, false, false, true,  false, false, false, true,  false},
            {false, false, true,  false, false, false, false, true,  false, false}
        };
        poblarAsteroidesConMatriz(mapa);
    }
}
