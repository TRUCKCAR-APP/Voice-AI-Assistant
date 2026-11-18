# Voice-AI-Assistant

Intérprete con IA (Azure OpenAI) de las órdenes de voz del **Copiloto TruckCar**.

El Copiloto de la app del conductor transcribe la voz **en el teléfono**: reconocimiento nativo de Android en la APK y Web Speech API en el navegador. Sus reglas locales (`js/services/voiceIntents.js`) entienden las órdenes habituales sin internet: "reportar derrumbe", "recalcular ruta", "qué llevo".

Cuando esas reglas no entienden una frase ("se vino la montaña encima de la vía", "llévame por otro lado"), el Copiloto envía **solo el texto** a este servicio. El servicio responde con una de las intenciones que la app ya sabe ejecutar, y la app la ejecuta con sus confirmaciones habladas de siempre.

```
voz ─► STT del teléfono ─► reglas locales ─┬─► acción (con confirmación)
                                           └─ no entendió ─► POST /api/v1/assistant/interpret ─► intención ─┘
```

## API

`POST /api/v1/assistant/interpret` se llama a través del api-gateway con el JWT del conductor; el gateway agrega `X-Auth-User`.

```json
{ "text": "se vino la montaña encima de la vía", "context": { "hasLoad": true, "navigating": true, "loadStatus": "IN_TRANSIT" } }
```

```json
{ "intent": "REPORT", "alertType": "landslide", "closure": false, "source": "AZURE_OPENAI" }
```

**Validación de la petición:** `text` es obligatorio y tiene como máximo 300 caracteres. Si falla, responde 400 (RFC 7807). Sin `X-Auth-User` responde 401.

**Intenciones:** `REPORT`, `SOS`, `NEARBY_ALERTS`, `ACCEPT_LOAD`, `REJECT_LOAD`, `START_TRIP`, `CONFIRM_DELIVERY`, `REROUTE`, `REPEAT`, `HOURS`, `LOAD_INFO`, `HELP`, `CHAT` (respuesta hablada sin acción) y `UNKNOWN`.

**Campos según la intención:**

| Intención | Campo | Valores |
|---|---|---|
| `REPORT` | `alertType` | `landslide`, `danger`, `workshop`, `police`, `scale`, `bridge`, `rest`, `traffic` |
| `REPORT` de tipo `workshop` | `closure` | `true` o `false` |
| `LOAD_INFO` | `field` | `eta`, `destination`, `weight`, `summary` |
| `REROUTE` | `avoidAhead` | `true` o `false` |
| `CHAT`, `HELP`, `UNKNOWN` | `reply` | texto para leer en voz alta |

## Seguridad

- **El audio nunca sale del teléfono:** el servicio recibe texto.
- **El texto del conductor es un dato:** va como mensaje de usuario, nunca dentro de las instrucciones del sistema.
- **Lo que responde el modelo se valida contra listas cerradas.** Una intención, un tipo de alerta o un campo desconocido se convierte en `UNKNOWN` (o `summary`). Las respuestas habladas se limpian y se cortan a 240 caracteres.
- **El servicio no llama a ningún otro servicio ni cambia nada.** SOS, aceptar o rechazar una carga, iniciar el viaje, la entrega y los reportes que vienen de la IA los confirma el conductor de viva voz en la app.
- **Solo se llega a través del gateway:** no publica puertos fuera de `truckcar-network`.

## Comportamiento cuando algo falta

- **Sin Azure OpenAI** (sin endpoint, sin clave o con los valores de relleno del docker-compose): el servicio arranca y responde `UNKNOWN`, y el Copiloto sigue con sus reglas.
- **Si Azure no responde a tiempo** (4 s, `TRUCKCAR_VOICE_AI_TIMEOUT`): responde lo mismo.
- **Memoria de la conversación:** se guardan los últimos 3 turnos por conductor durante 10 minutos en Redis (`voice_context:<id>`), para entender "¿y cuánto me falta?" después de "¿a dónde voy?". Si Redis no está, se sigue sin historial; el health de Redis está deshabilitado a propósito.

## Configuración

| Variable | Uso |
|---|---|
| `AZURE_OPENAI_ENDPOINT`, `AZURE_OPENAI_KEY` | El mismo recurso que Route-AI-agent (del `.env`, nunca en el código) |
| `AZURE_OPENAI_DEPLOYMENT` | Despliegue del modelo (por defecto `gpt-4.1-mini`) |
| `SPRING_DATA_REDIS_HOST`, `SPRING_DATA_REDIS_PORT` | Redis de la plataforma |
| `TRUCKCAR_VOICE_AI_TIMEOUT` | Tiempo máximo de la llamada al modelo (por defecto `4s`) |

- **Puerto:** 8088.
- **Métricas:** `/actuator/prometheus`, con el contador `truckcar_voice_interpretations_total{intent,source}`.
- **Trazas:** OTLP.

## Desarrollo

```bash
./mvnw clean verify          # 23 pruebas, sin Azure, Redis ni base de datos
```

En la plataforma completa, el servicio se levanta con el `docker-compose.yml` de Deployment-Infrastructure como `voice-ai-assistant`.
