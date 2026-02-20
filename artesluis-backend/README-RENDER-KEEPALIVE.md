# Soluciones para Evitar Sleep Mode en Render (Plan Free)

## Problema
Render en el plan gratuito pone el servicio en "sleep" después de 15 minutos de inactividad. El servicio tarda ~30-50 segundos en "despertar" con la primera petición.

## Soluciones Disponibles

### ⭐ Solución 1: UptimeRobot (Recomendada - Gratis)
UptimeRobot es un servicio gratuito que hace ping a tu aplicación cada 5 minutos.

**Pasos:**
1. Regístrate en [UptimeRobot](https://uptimerobot.com)
2. Crea un nuevo monitor con:
   - **Monitor Type:** HTTP(s)
   - **URL:** `https://tu-app.onrender.com/api/test`
   - **Monitoring Interval:** 5 minutos
   - **Alert Contacts:** Tu email (opcional)
3. Guarda y activa el monitor

**Ventajas:**
- ✅ Completamente gratis
- ✅ Fácil de configurar
- ✅ Dashboard para ver el estado
- ✅ Alertas por email si el servicio está caído

---

### Solución 2: Cron-Job.org (Alternativa)
Servicio similar a UptimeRobot.

**Pasos:**
1. Regístrate en [Cron-Job.org](https://cron-job.org)
2. Crea un nuevo cronjob:
   - **URL:** `https://tu-app.onrender.com/api/test`
   - **Schedule:** `*/5 * * * *` (cada 5 minutos)
3. Activa el cronjob

---

### Solución 3: Servicio de Ping Personalizado (Avanzada)
Si tienes otro servidor o servicio corriendo 24/7, puedes crear un simple script que haga ping.

**Ejemplo con Node.js:**
```javascript
// keep-alive.js
const https = require('https');

const RENDER_URL = 'https://tu-app.onrender.com/api/test';
const INTERVAL = 5 * 60 * 1000; // 5 minutos

function ping() {
    https.get(RENDER_URL, (res) => {
        console.log(`[${new Date().toISOString()}] Ping exitoso - Status: ${res.statusCode}`);
    }).on('error', (err) => {
        console.error(`[${new Date().toISOString()}] Error en ping:`, err.message);
    });
}

// Ping inicial
ping();

// Ping cada 5 minutos
setInterval(ping, INTERVAL);
```

---

### Solución 4: GitHub Actions (Gratuita - Requiere repo GitHub)
Si tu proyecto está en GitHub, puedes usar GitHub Actions para hacer ping automático.

**Crear archivo:** `.github/workflows/keep-alive.yml`
```yaml
name: Keep Alive

on:
  schedule:
    # Ejecutar cada 5 minutos (*/5 * * * *)
    # NOTA: GitHub Actions tiene límite de 1 acción cada 5 minutos para cron jobs
    - cron: '*/5 * * * *'
  workflow_dispatch: # Permite ejecutar manualmente

jobs:
  keep-alive:
    runs-on: ubuntu-latest
    steps:
      - name: Ping Render Service
        run: |
          curl -I https://tu-app.onrender.com/api/test
          echo "Ping completado: $(date)"
```

**Nota:** GitHub Actions tiene limitaciones para jobs frecuentes. No es la solución más confiable para mantener servicios activos 24/7.

---

### Solución 5: Actualizar a Plan de Pago
Si el proyecto es comercial o importante, considera actualizar a un plan de pago en Render (~$7/mes para el plan Starter).

**Ventajas:**
- ✅ Sin sleep mode
- ✅ Más recursos (RAM, CPU)
- ✅ Mejor soporte

---

## Recomendación Final

Para tu caso, **usa UptimeRobot**:
1. Es gratis y confiable
2. Fácil de configurar (5 minutos)
3. Dashboard para monitoreo
4. Funciona perfectamente con Render Free

## Endpoint de Health Check
Tu aplicación ya tiene un endpoint `/api/test` que responde con status 200. Este es perfecto para los servicios de ping.

Si quieres crear uno más específico, puedes agregar:

```java
@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
```

---

## Notas Importantes

⚠️ **Limitaciones del Plan Free de Render:**
- 750 horas/mes de tiempo activo (suficiente para 1 servicio 24/7)
- Después de 15 minutos sin actividad → sleep mode
- ~30-50 segundos para despertar

⚠️ **Consideraciones:**
- No abuses de los servicios de ping (5 minutos es un intervalo razonable)
- Si el tráfico de tu aplicación es alto, eventualmente no necesitarás ping
- El sleep mode solo afecta al plan free, no a planes de pago
