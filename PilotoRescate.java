import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class PilotoRescate extends PilotoBase {

    private ContenedorDeRecursos[] bodega = new ContenedorDeRecursos[10];
    private int cantidadEnBodega = 0;
    private Direccion direccionActual = Direccion.NORTE;
    private int[][] sectoresVisitados; // [filas][columnas] = [alto][ancho]
    private boolean misionIniciada = false;
    private int pasosTrabada = 0;
    private boolean enCombate = false;
    private Actor objetivoCombate = null;

    @Override
    public void subirse(NaveDeAtaque nave) {
        super.subirse(nave);
        int filas = nave.getWorld().getHeight();
        int columnas = nave.getWorld().getWidth();
        sectoresVisitados = new int[filas][columnas];
    }

    @Override
    public void bajarse() {
        super.bajarse();
    }

    public void act() {
        if (navePilotada == null) return;
        
        navePilotada.encenderMotores();
        
        if (misionIniciada && navePilotada.obtenerCombustible() < 7) {
            imprimirReporteFinal();
            navePilotada = null;
            return;
        }
        
        if (navePilotada.motoresEncendidos()) {
            misionIniciada = true;
        }
        
        revisarContenedorBajoNave();
        
        // Si está en combate, terminar primero antes de buscar combustible
        if (enCombate || hayNaveEnemigaAdyacente()) {
            if (manejarAtaqueANaves()) return;
        }
        
        // Buscar combustible solo si no está en combate
        if (navePilotada.obtenerCombustible() < 70) {
            if (manejarBusquedaCombustible()) return;
        }
        
        revisarContenedorBajoNave();
        if (manejarRecoleccion()) return;
        if (manejarDestruccionMinerales()) return;  // ← nuevo
        if (manejarDestruccionAsteroides()) return;
        manejarAtaqueANaves();
        manejarNavegacion();
        registrarSector();
    }
    
    private boolean hayNaveEnemigaAdyacente() {
        Direccion[] dirs = {Direccion.NORTE, Direccion.SUR, Direccion.ESTE, Direccion.OESTE};
        for (Direccion dir : dirs) {
            if (navePilotada.hayNaveHacia(dir)) return true;
        }
        return false;
    }
    
    private boolean manejarBusquedaCombustible() {
        // Busca el ItemDeCombustible más cercano en el mundo
        java.util.List items = navePilotada.getWorld().getObjects(ItemDeCombustible.class);
        if (items.isEmpty()) return false;
        
        int xNave = navePilotada.getX();
        int yNave = navePilotada.getY();
        
        Actor masСercano = null;
        int menorDistancia = Integer.MAX_VALUE;
        
        for (Object obj : items) {
         Actor item = (Actor) obj;
            int dist = Math.abs(item.getX() - xNave) + Math.abs(item.getY() - yNave);
            if (dist < menorDistancia) {
                menorDistancia = dist;
                masСercano = item;
            }
        }
        
        if (masСercano == null) return false;
        
        // Moverse hacia él
        int dx = masСercano.getX() - xNave;
        int dy = masСercano.getY() - yNave;
        
        Direccion dir;
        if (Math.abs(dx) >= Math.abs(dy)) {
            dir = dx > 0 ? Direccion.ESTE : Direccion.OESTE;
        } else {
            dir = dy > 0 ? Direccion.SUR : Direccion.NORTE;
        }
        
        if (puedoAvanzarHacia(dir)) {
            direccionActual = dir;
            navePilotada.avanzarHacia(dir);
            return true;
        }
        return false;
    }
    
    // Ataca naves enemigas adyacentes
    private boolean manejarAtaqueANaves() {
        if (navePilotada.obtenerCombustible() < 40) return false;
        
        Direccion[] dirs = {Direccion.NORTE, Direccion.SUR, Direccion.ESTE, Direccion.OESTE};
        
        // Si hay nave adyacente, atacarla
        for (Direccion dir : dirs) {
            if (navePilotada.hayNaveHacia(dir)) {
                enCombate = true;
                // Guardar referencia al objetivo
                java.util.List naves = navePilotada.getWorld().getObjects(NaveEnemiga.class);
                for (Object obj : naves) {
                    Actor nave = (Actor) obj;
                    if (nave.getX() == navePilotada.getX() + dir.dx && 
                        nave.getY() == navePilotada.getY() + dir.dy) {
                        objetivoCombate = nave;
                        break;
                    }
                }
                navePilotada.atacarHacia(dir);
                return true;
            }
        }
        
        // Si estaba en combate pero la nave no está adyacente, perseguirla
        if (enCombate && objetivoCombate != null && objetivoCombate.getWorld() != null) {
            int dx = objetivoCombate.getX() - navePilotada.getX();
            int dy = objetivoCombate.getY() - navePilotada.getY();
            Direccion dir = Math.abs(dx) >= Math.abs(dy)
                ? (dx > 0 ? Direccion.ESTE : Direccion.OESTE)
                : (dy > 0 ? Direccion.SUR : Direccion.NORTE);
            if (puedoAvanzarHacia(dir)) {
                direccionActual = dir;
                navePilotada.avanzarHacia(dir);
                return true;
            }
        }
        
        // No hay nave enemiga, terminar combate
        enCombate = false;
        objetivoCombate = null;
        return false;
    }
    
    // Si el camino está bloqueado por un asteroide, lo destruye en vez de esquivarlo
    private boolean manejarDestruccionAsteroides() {
        if (navePilotada.obtenerCombustible() < 30) return false; // no destruir si hay poco combustible
        
        if (!puedoAvanzarHacia(direccionActual) && navePilotada.hayAsteroideHacia(direccionActual)) {
            navePilotada.atacarHacia(direccionActual);
            return true;
        }
        return false;
    }
    
    private boolean manejarDestruccionMinerales() {
        if (navePilotada.obtenerCombustible() < 40) return false;
        
        Direccion[] dirs = {Direccion.NORTE, Direccion.SUR, Direccion.ESTE, Direccion.OESTE};
        
        // Si hay un AsteroideDeMineral adyacente, atacarlo
        for (Direccion dir : dirs) {
            if (navePilotada.hayAsteroideHacia(dir)) {
                // Verificar que sea específicamente un AsteroideDeMineral
                java.util.List minerales = navePilotada.getWorld().getObjects(AsteroideDeMineral.class);
                for (Object obj : minerales) {
                    Actor mineral = (Actor) obj;
                    if (mineral.getX() == navePilotada.getX() + dir.dx &&
                        mineral.getY() == navePilotada.getY() + dir.dy) {
                        navePilotada.atacarHacia(dir);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void revisarContenedorBajoNave() {
        ContenedorDeRecursos contenedor = navePilotada.obtenerContenedorEnPosicion();
            if (contenedor != null) {
                // Primero lo sacamos del mundo para que no se detecte dos veces
                contenedor.serRecogido();
                
                boolean guardado = guardarEnBodega(contenedor);
                if (guardado) {
                    System.out.println("Contenedor recogido: " + contenedor.getCodigoManifiesto() 
                        + " | Créditos: " + contenedor.getCreditos()
                        + " | Bodega: " + cantidadEnBodega + "/10");
                } else {
                    System.out.println("Bodega llena, no se pudo recoger: " + contenedor.getCodigoManifiesto());
                }
            }
        }
    // Si hay un contenedor en alguna direccion, ir hacia el
    private boolean manejarRecoleccion() {
            Direccion[] todasLasDirecciones = {
            Direccion.NORTE, Direccion.SUR, Direccion.ESTE, Direccion.OESTE
        };
        for (Direccion dir : todasLasDirecciones) {
            if (navePilotada.hayItemHacia(dir)) {
                direccionActual = dir;
                navePilotada.avanzarHacia(dir);
                return true; // encontró item, detiene el paso
            }
        }
        return false; // no había item cerca
    }

    // Navegación basica: avanzar o girar si hay obstaculo
    private void manejarNavegacion() {
        if (puedoAvanzarHacia(direccionActual)) {
            navePilotada.avanzarHacia(direccionActual);
            pasosTrabada = 0;
        } else {
            pasosTrabada++;
            
            Direccion[] opciones = {
                direccionActual.derecha(),
                direccionActual.izquierda(),
                direccionActual.opuesta(),
                direccionActual.derecha().derecha()
            };
    
            boolean movio = false;
            for (Direccion opcion : opciones) {
                if (puedoAvanzarHacia(opcion)) {
                    direccionActual = opcion;
                    navePilotada.avanzarHacia(direccionActual);
                    movio = true;
                    pasosTrabada = 0;
                    break;
                }
            }
    
            if (!movio) {
                Direccion[] todas = {Direccion.NORTE, Direccion.SUR, Direccion.ESTE, Direccion.OESTE};
                for (Direccion d : todas) {
                    if (puedoAvanzarHacia(d)) {
                        direccionActual = d;
                        navePilotada.avanzarHacia(direccionActual);
                        pasosTrabada = 0;
                        break;
                    }
                }
            }
        }
    
        // Si lleva muchos pasos dando vueltas, cambiar dirección a la opuesta
        if (pasosTrabada > 5) {
            direccionActual = direccionActual.opuesta();
            pasosTrabada = 0;
        }
    }

    // Devuelve true si la direccion esta libre (sin borde, sin asteroide, sin nave)
    private boolean puedoAvanzarHacia(Direccion dir) {
        return !navePilotada.hayVacioHacia(dir)
            && !navePilotada.hayAsteroideHacia(dir)
            && !navePilotada.hayNaveHacia(dir);
    }

    private boolean guardarEnBodega(ContenedorDeRecursos contenedor) {
        if (cantidadEnBodega >= bodega.length) {
            return false;
        }
        bodega[cantidadEnBodega] = contenedor;
        cantidadEnBodega++;
        return true;
    }
    
    private void registrarSector() {
        int x = navePilotada.getX(); // columna
        int y = navePilotada.getY(); // fila
        sectoresVisitados[y][x]++;
    }
    
    private void imprimirReporteFinal() {
        System.out.println("========== REPORTE FINAL DE MISION ==========");
        
        // 1. Balance economico
        int totalCreditos = 0;
        for (int i = 0; i < cantidadEnBodega; i++) {
            totalCreditos += bodega[i].getCreditos();
        }
        System.out.println("1. Creditos totales rescatados: " + totalCreditos);
        
        // 2. Espacios vacios en la bodega
        int espaciosVacios = bodega.length - cantidadEnBodega;
        System.out.println("2. Espacios vacios en bodega: " + espaciosVacios + "/10");
        
        // 3. El contenedor mas caro
        if (cantidadEnBodega > 0) {
            int maxCreditos = bodega[0].getCreditos();
            String maxCodigo = bodega[0].getCodigoManifiesto();
            for (int i = 1; i < cantidadEnBodega; i++) {
                if (bodega[i].getCreditos() > maxCreditos) {
                    maxCreditos = bodega[i].getCreditos();
                    maxCodigo = bodega[i].getCodigoManifiesto();
                }
            }
            System.out.println("3. Contenedor mas valioso: " + maxCodigo + " (" + maxCreditos + " creditos)");
        } else {
            System.out.println("3. No se rescato ningun contenedor.");
        }
        
        // 4. Buscar cristal OMEGA
        boolean omegaEncontrado = false;
        for (int i = 0; i < cantidadEnBodega; i++) {
            if (bodega[i].getCodigoManifiesto().contains("OMEGA")) {
                omegaEncontrado = true;
                System.out.println("4. Cristal OMEGA localizado: " + bodega[i].getCodigoManifiesto());
            }
        }
        if (!omegaEncontrado) {
            System.out.println("4. ALERTA: Cristal OMEGA no encontrado, perdido en el espacio.");
        }
        
        // 5. Matriz de sectores visitados
        System.out.println("5. Mapa de sectores transitados:");
        for (int fila = 0; fila < sectoresVisitados.length; fila++) {
            String linea = "   ";
            for (int col = 0; col < sectoresVisitados[fila].length; col++) {
                linea += "[" + sectoresVisitados[fila][col] + "]";
            }
            System.out.println(linea);
        }
        
        System.out.println("=============================================");
    }
}
