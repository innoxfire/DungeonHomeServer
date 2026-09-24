# Runic Terminal — design e roadmap

## Obiettivo

Runic Terminal e' un client Android per amministrare in sicurezza macchine Linux
personali tramite SSH, raggiunte esclusivamente attraverso il tailnet Tailscale.
Il terminale offre una shell libera, ma con un tema grafico ispirato al golem
runico e con chiavi SSH protette da autenticazione biometrica.

## Confine delle responsabilita'

| Componente | Responsabilita' |
| --- | --- |
| Tailscale | Rete privata cifrata fra telefono e macchine. L'app Tailscale ufficiale gestisce login e VPN. |
| Runic Terminal | Profili macchina, sblocco biometrico, connessione SSH, terminale e interfaccia grafica. |
| OpenSSH sul server | Autenticazione della chiave pubblica e shell dell'utente Linux. |
| Termux (opzionale) | Apertura di una shell esterna per sviluppo o emergenza; non custodisce le chiavi dell'app. |

Non incorporare l'app Termux dentro Runic Terminal. Termux espone un'intenzione
per eseguire comandi, ma richiede un permesso esplicito per l'esecuzione di
comandi arbitrari e l'abilitazione `allow-external-apps`. E' adatto al pulsante
"Apri in Termux", non a un terminale integrato sicuro.

## Esperienza utente

1. L'utente sceglie una macchina nella schermata Runic Connection.
2. L'app controlla che il nodo sia raggiungibile nel tailnet.
3. L'utente conferma l'impronta o il PIN del dispositivo.
4. Android Keystore sblocca il materiale SSH cifrato solo per la sessione.
5. L'app verifica la host key SSH della macchina.
6. Si apre il terminale libero.
7. Alla disconnessione, l'app chiude la sessione e cancella i segreti dalla
   memoria quanto possibile.

## Tema grafico: specifica recuperabile

### Regole non negoziabili

- Il contenuto del terminale deve restare testo monospaziato autentico.
- Non sostituire comandi, percorsi, output o caratteri copiabili con rune.
- Il contrasto del testo normale deve restare alto anche con lo sfondo attivo.
- Il colore non deve essere l'unico mezzo per indicare errore, successo o stato.
- Offrire un interruttore "Contrasto massimo" che rimuove l'illustrazione.

### Composizione

```text
┌ [runa] Home Server                    [Tailscale: connesso] ┐
│ utente@home-server  ~/docker                                  │
│ ❯ docker compose ps                                            │
│ ... output della shell ...                                     │
│                                                                  │
│ [Ctrl] [Esc] [Tab] [↑] [↓] [←] [→]                    [tastiera]│
└──────────────────────────────────────────────────────────────────┘
```

- Barra superiore: runa dell'icona Runic Terminal, nome macchina,
  stato Tailscale e stato SSH.
- Piano testo: sfondo carbone con lieve tinta verde/oliva.
- Illustrazione: golem in basso o lateralmente, sfocato, desaturato e al
  5–10% di opacita'. Mai dietro alla riga di input senza uno scrim uniforme.
- Barra dei tasti speciali: parte dell'interfaccia dell'app, non dell'output
  del server.
- La cornice fantasy deve delimitare la card o la barra di stato, non ridurre
  l'area utile del terminale.

### Palette iniziale

| Ruolo | Colore suggerito | Uso |
| --- | --- | --- |
| Sfondo | `#151A17` | Carbone verde, mai nero puro |
| Testo | `#F5EBDD` | Testo e output normali |
| Prompt/runa | `#64DEC9` | Prompt, connessione attiva |
| Utente/host | `#FFC46B` | Identita' della macchina |
| Directory | `#F19A57` | Percorso corrente |
| Input comando | `#FFF1D4` | Testo digitato dall'utente |
| Attenzione | `#E88A57` | Avvisi piu' icona |
| Errore | `#D86B62` | Errori piu' icona e messaggio |

### Tipografia e comportamento

- Usare un font monospaziato leggibile e senza ambiguita' fra `0/O` e `1/l/I`.
- Disattivare le ligature per default: il terminale deve mostrare i byte in
  modo prevedibile.
- Runa solo nel prompt, nello stato e nei separatori; il cursore resta un
  blocco o una linea ben visibile.
- Evidenziare durante la digitazione solo il primo token che sembra un comando
  e i flag. Non reinterpretare l'output remoto.
- Conservare e rispettare i colori ANSI mandati dalla shell remota, offrendo
  una mappatura del tema per gli otto colori base.
- Includere dimensione font, spaziatura, contrasto massimo e riduzione delle
  animazioni nelle impostazioni.

## Struttura del progetto Android

Non serve un file Kotlin per ogni progetto grafico. Serve un package per ogni
responsabilita'; un file puo' contenere un piccolo gruppo coerente di tipi.
`MainActivity` deve solo avviare Compose e ospitare la navigazione.

```text
com.example.dungeonhomeserver/
  MainActivity.kt                    # bootstrap, nessuna logica SSH
  navigation/
    AppNavigation.kt                 # destinazioni e back stack
  core/
    model/                           # MachineProfile, ConnectionState
    designsystem/                    # colori, componenti runici, tema
  feature/
    portal/                          # landing e selezione progetti
    runicterminal/
      RunicTerminalScreen.kt         # composable della schermata
      RunicTerminalViewModel.kt      # stato della schermata
      TerminalTheme.kt               # palette e preferenze visuali
      TerminalKeyboardBar.kt         # Ctrl, Esc, Tab, frecce
      MachinePickerScreen.kt         # scelta della macchina
  data/
    machines/                        # profili non segreti e repository
    ssh/                             # futura implementazione SSH
    security/                        # vault cifrato e host-key store
    tailscale/                       # stato/reachability, non VPN propria
```

All'inizio questi possono essere soltanto package. I moduli Gradle separati
sono utili piu' avanti, quando il terminale e la rete crescono.

## Scelte tecniche da mantenere

### Rete

- Il telefono e ogni macchina appartengono allo stesso tailnet Tailscale.
- Ogni server avvia Tailscale automaticamente al boot.
- L'app tenta la connessione SSH al nome MagicDNS della macchina; non apre
  porte sul router e non dipende da IP di casa.
- L'app puo' aprire Tailscale quando la VPN non e' attiva, ma non deve gestire
  login, token di amministrazione o implementare una VPN propria.

### Chiavi SSH

- Una coppia Ed25519 per ciascuna macchina.
- Privata sul telefono; pubblica nel file `authorized_keys` del relativo
  utente Linux.
- Il blob della chiave privata e della relativa passphrase viene cifrato con
  una chiave Android Keystore che richiede biometria/PIN.
- Nessuna passphrase in chiaro nei file, negli Intent, nei log o negli script.
- Alla prima connessione si mostra e si salva la fingerprint della host key;
  un cambiamento successivo richiede conferma esplicita.

### Terminale libero

- Terminale integrato: una libreria SSH produce un canale shell interattivo;
  un emulatore terminale interpreta ANSI/VT e la UI Compose disegna il buffer.
- I componenti devono restare separati: trasporto SSH, emulazione terminale e
  grafica non devono conoscersi direttamente.
- Termux rimane un fallback: "Apri in Termux" lancia un alias o script
  predefinito. Non gli vengono passati chiavi o passphrase dell'app.

## Roadmap di apprendimento e implementazione

### Fase 0 — mettere in ordine l'interfaccia

Obiettivo: spostare il codice attuale fuori da `MainActivity` senza aggiungere
rete o segreti.

Risultato: Portal, Project Picker e Runic Terminal sono schermate separate;
la selezione macchina usa dati finti.

### Fase 1 — modello e stato UI

Obiettivo: capire `data class`, stato Compose, ViewModel e navigazione.

Risultato: una `MachineProfile` finta passa dalla lista al dettaglio; gli stati
"VPN non attiva", "pronto", "connessione" e "errore" sono visibili.

### Fase 2 — terminale finto ma interattivo

Obiettivo: realizzare prima tastiera speciale, buffer, scroll e tema senza
una rete reale.

Risultato: una console locale simulata riceve input, mostra output dimostrativo
e conserva l'aspetto runico. Questa fase rende verificabile la UX.

### Fase 3 — laboratorio Linux e Tailscale

Obiettivo: imparare fuori dall'app il flusso SSH completo.

Risultato: un utente Linux non-root, una chiave per un solo server, login SSH
solo via Tailscale, host key verificata e nessuna porta SSH pubblica.

### Fase 4 — persistenza sicura dei profili

Obiettivo: memorizzare alias, host, utente e fingerprint; poi introdurre il
vault protetto da biometria.

Risultato: l'app non conserva ancora chiavi in chiaro e sa rilevare una host
key cambiata.

### Fase 5 — prima sessione SSH reale

Obiettivo: collegare una sola macchina e inviare/ricevere testo da una shell.

Risultato: connessione, chiusura, timeout e messaggi d'errore gestiti senza
bloccare la UI.

### Fase 6 — terminale completo

Obiettivo: ANSI/VT, resize, tasti speciali, copia/incolla, piu' tab e SFTP.

Risultato: terminale libero usabile per amministrazione reale.

### Fase 7 — integrazione opzionale Termux e OpenCloud

Obiettivo: handoff verso Termux e comandi amministrativi controllati;
successivamente deployment privato di OpenCloud sul tailnet.

Risultato: nessun segreto dell'app transita verso Termux e nessun servizio e'
esposto pubblicamente.

## Cose da non fare nella prima versione

- Non aggiungere una VPN proprietaria.
- Non memorizzare password o chiavi in `SharedPreferences`, file testuali o
  repository Git.
- Non eseguire comandi shell costruiti da testo libero passato a Termux.
- Non usare l'utente `root` come utente SSH quotidiano.
- Non esporre OpenCloud con Tailscale Funnel o port forwarding mentre si sta
  imparando.
- Non implementare insieme SSH, Tailscale, biometria e terminale: ogni fase
  deve essere verificabile da sola.

## Riferimenti

- https://developer.android.com/topic/architecture
- https://developer.android.com/topic/architecture/ui-layer
- https://developer.android.com/privacy-and-security/keystore
- https://tailscale.com/docs/install/android
- https://tailscale.com/docs/features/tailscale-serve
- https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent
