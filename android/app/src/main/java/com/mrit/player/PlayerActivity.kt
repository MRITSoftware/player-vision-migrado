package com.mrit.player

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.TextView
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var imageView: ImageView
    private lateinit var codigoScreenContainer: LinearLayout
    private lateinit var logoContainer: LinearLayout
    private lateinit var codigoInputContainer: LinearLayout
    private lateinit var rodapeContainer: LinearLayout
    private lateinit var codigoEditText: EditText
    private lateinit var btnConfirmarCodigo: Button
    private lateinit var rodapeTexto: TextView
    private lateinit var promoOverlay: FrameLayout
    private lateinit var promoImage: ImageView
    private lateinit var promoText: TextView
    private lateinit var promoOriginalPrice: TextView
    private lateinit var promoPrice: TextView
    private lateinit var promoCounterView: TextView
    private var exoPlayer: ExoPlayer? = null

    private val playlist: MutableList<PlaylistItem> = mutableListOf()
    private var currentIndex = 0
    private var isShowingVideo = false

    private lateinit var downloadManager: VideoDownloadManager
    private var heartbeatJob: Job? = null
    private var playlistWatchJob: Job? = null
    private var promoWatchJob: Job? = null
    private var deviceCommandJob: Job? = null
    private var currentPlaylistSignature: String? = null
    private var currentCodigo: String? = null
    private var currentCodigoConteudo: String? = null
    private var currentPromotion: PromotionData? = null
    private var pendingPlaylist: List<PlaylistItem>? = null
    private var pendingPlaylistSignature: String? = null
    private var hideImageOnNextVideoFrame: Boolean = false

    private val nextRunnable = Runnable {
        nextItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        applyImmersiveFullscreen()
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.videoView)
        imageView = findViewById(R.id.imageView)
        codigoScreenContainer = findViewById(R.id.codigoScreenContainer)
        logoContainer = findViewById(R.id.logoContainer)
        codigoInputContainer = findViewById(R.id.codigoInputContainer)
        rodapeContainer = findViewById(R.id.rodapeContainer)
        codigoEditText = findViewById(R.id.codigoInput)
        btnConfirmarCodigo = findViewById(R.id.btnConfirmarCodigo)
        rodapeTexto = findViewById(R.id.rodapeTexto)
        promoOverlay = findViewById(R.id.promoOverlay)
        promoImage = findViewById(R.id.promoImage)
        promoText = findViewById(R.id.promoText)
        promoOriginalPrice = findViewById(R.id.promoOriginalPrice)
        promoPrice = findViewById(R.id.promoPrice)
        promoCounterView = findViewById(R.id.promoCounter)

        initPlayer()
        downloadManager = VideoDownloadManager(this)

        // Configurar botão de confirmação de código
        btnConfirmarCodigo.setOnClickListener {
            val codigo = codigoEditText.text.toString().trim().uppercase()
            if (codigo.isNotBlank()) {
                validarEAplicarCodigo(codigo)
            } else {
                Toast.makeText(this, "Informe o código da tela", Toast.LENGTH_SHORT).show()
            }
        }

        // Verificar se já existe código salvo (equivalente a verificarCodigoSalvo local)
        val prefs = getSharedPreferences("mrit_prefs", MODE_PRIVATE)
        val codigoSalvo = prefs.getString("display_codigo", null)
        if (!codigoSalvo.isNullOrBlank()) {
            codigoEditText.setText(codigoSalvo)
            // Ao abrir com código salvo, também podemos validar com o backend
            validarEAplicarCodigo(codigoSalvo)
        } else {
            aplicarEstadoSemCodigo()
        }
    }

    override fun onResume() {
        super.onResume()
        applyImmersiveFullscreen()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) applyImmersiveFullscreen()
    }

    private fun applyImmersiveFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun validarEAplicarCodigo(codigo: String) {
        val ctx = this
        lifecycleScope.launch {
            val deviceId = DeviceIdProvider.getDeviceId(ctx)
            val deviceService = SupabaseDeviceService()
            var codigoConteudoInicial: String? = null

            val resultado = withContext(Dispatchers.IO) {
                // 1) Verificar se o display existe
                val display = deviceService.getDisplay(codigo)
                if (display == null) {
                    return@withContext "Código inválido ou não encontrado"
                }

                // 2) Bloquear apenas se realmente está locked por outro device ainda ativo.
                val mesmoDispositivo = !display.deviceId.isNullOrBlank() && display.deviceId == deviceId
                val lockStale = isDisplayLockStale(display.deviceLastSeen)
                if (display.isLocked == true && !mesmoDispositivo && !lockStale) {
                    return@withContext "Código já está em uso em outro dispositivo"
                }

                codigoConteudoInicial = display.codigoConteudoAtual?.trim().takeUnless { it.isNullOrBlank() } ?: codigo
                // Se chegou aqui, consideramos válido para este dispositivo
                null
            }

            if (resultado != null) {
                Toast.makeText(ctx, resultado, Toast.LENGTH_LONG).show()
                aplicarEstadoSemCodigo()
            } else {
                salvarCodigoLocal(codigo)
                currentCodigo = codigo
                aplicarEstadoComCodigo(codigo)

                // Marcar display em uso e iniciar heartbeat
                iniciarHeartbeat(codigo)
                iniciarWatchDePromocao(codigo)
                iniciarWatchDeComandos()
                carregarPlaylistDoBackend(codigoConteudoInicial ?: codigo, codigo)
            }
        }
    }

    private fun iniciarHeartbeat(codigo: String) {
        heartbeatJob?.cancel()
        val ctx = this
        heartbeatJob = lifecycleScope.launch {
            val deviceId = DeviceIdProvider.getDeviceId(ctx)
            val service = SupabaseDeviceService()

            // Atualização inicial (equivalente a "Em uso" + device_id)
            withContext(Dispatchers.IO) {
                service.marcarDisplayEmUso(codigo, deviceId)
            }

            // Heartbeat periódico
            while (isActive) {
                delay(5000) // 5s para refletir mudanças no painel quase em tempo real.
                val display = withContext(Dispatchers.IO) {
                    service.getDisplay(codigo)
                }

                // Se destravou no painel (is_locked=false), parar player e voltar à tela de código.
                if (display?.isLocked == false) {
                    pararTudoMostrarLogin(limparCodigoSalvo = true)
                    break
                }

                val novoCodigoConteudo = display?.codigoConteudoAtual?.trim().takeUnless { it.isNullOrBlank() } ?: codigo
                if (!novoCodigoConteudo.isNullOrBlank() && novoCodigoConteudo != currentCodigoConteudo) {
                    currentCodigoConteudo = novoCodigoConteudo
                    carregarPlaylistDoBackend(novoCodigoConteudo, codigo)
                }

                withContext(Dispatchers.IO) {
                    service.enviarHeartbeat(codigo, deviceId)
                }
            }
        }
    }

    private fun salvarCodigoLocal(codigo: String) {
        val prefs = getSharedPreferences("mrit_prefs", MODE_PRIVATE)
        prefs.edit()
            .putString("display_codigo", codigo)
            .apply()
    }

    private fun aplicarEstadoComCodigo(codigo: String) {
        // Esconder "tela de login" e deixar player full
        codigoScreenContainer.visibility = View.GONE
        codigoInputContainer.visibility = View.GONE
        rodapeContainer.visibility = View.GONE
        logoContainer.visibility = View.GONE

        // Só exibe vídeo/imagem quando houver item tocando para evitar tela preta
        playerView.visibility = View.GONE
        imageView.visibility = View.GONE

        // Atualizar texto de rodapé se quiser (ex: mostrar o código/local)
        rodapeTexto.text = "Código: $codigo"
    }

    private fun aplicarEstadoSemCodigo() {
        // Mostrar tela de entrada de código
        codigoScreenContainer.visibility = View.VISIBLE
        codigoInputContainer.visibility = View.VISIBLE
        rodapeContainer.visibility = View.VISIBLE
        logoContainer.visibility = View.VISIBLE

        // Esconder player até ter código
        playerView.visibility = View.GONE
        imageView.visibility = View.GONE

        heartbeatJob?.cancel()
        playlistWatchJob?.cancel()
        promoWatchJob?.cancel()
        deviceCommandJob?.cancel()

        // Marcar cache como não pronto ao sair
        currentCodigo?.let { codigo ->
            lifecycleScope.launch(Dispatchers.IO) {
                val service = SupabaseDeviceService()
                service.atualizarStatusCache(codigo, cached = false)
            }
        }

        currentCodigo = null
        currentCodigoConteudo = null
        pendingPlaylist = null
        pendingPlaylistSignature = null
        currentPromotion = null
        promoOverlay.visibility = View.GONE
    }

    private fun initPlayer() {
        val dataSourceFactory = buildCacheDataSource()

        exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build().also { player ->
            playerView.player = player
            player.volume = 0f // Player sempre mudo.
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED && isShowingVideo) {
                        nextItem()
                    }
                }

                override fun onRenderedFirstFrame() {
                    if (hideImageOnNextVideoFrame) {
                        imageView.visibility = View.GONE
                        hideImageOnNextVideoFrame = false
                    }
                }
            })
        }
    }

    private fun buildCacheDataSource(): DataSource.Factory {
        val cache = VideoCache.get(this)
        val upstream = DefaultHttpDataSource.Factory()
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstream)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private fun carregarPlaylistDoBackend(codigoConteudo: String, fallbackCodigo: String? = null) {
        val service = PlaylistService()
        lifecycleScope.launch {
            var codigoSelecionado = codigoConteudo
            var itens = withContext(Dispatchers.IO) {
                service.carregarPlaylist(codigoConteudo)
            }

            val fallback = fallbackCodigo?.trim()
            if (itens.isEmpty() && !fallback.isNullOrBlank() && fallback != codigoConteudo) {
                itens = withContext(Dispatchers.IO) {
                    service.carregarPlaylist(fallback)
                }
                if (itens.isNotEmpty()) {
                    codigoSelecionado = fallback
                }
            }

            if (itens.isEmpty()) {
                // Aceitar o código mesmo sem conteúdo e continuar monitorando.
                currentCodigoConteudo = codigoSelecionado
                playlist.clear()
                currentPlaylistSignature = null
                playerView.visibility = View.GONE
                imageView.visibility = View.GONE
                iniciarWatchDePlaylist(codigoSelecionado)
                return@launch
            }

            applyPlaylistImmediately(
                codigoConteudo = codigoSelecionado,
                newItems = itens,
                forceRestart = true
            )
        }
    }

    private fun buildPlaylistSignature(list: List<PlaylistItem>): String {
        return list.joinToString("|") {
            "${it.url}#${it.type}#${it.durationMs}#${it.urlPortrait}#${it.urlLandscape}"
        }
    }

    private fun isOnlyAdditionAtEnd(current: List<PlaylistItem>, incoming: List<PlaylistItem>): Boolean {
        if (incoming.size <= current.size) return false
        for (index in current.indices) {
            if (buildPlaylistSignature(listOf(current[index])) != buildPlaylistSignature(listOf(incoming[index]))) {
                return false
            }
        }
        return true
    }

    private fun applyPlaylistImmediately(
        codigoConteudo: String,
        newItems: List<PlaylistItem>,
        forceRestart: Boolean
    ) {
        currentCodigoConteudo = codigoConteudo
        playlist.clear()
        playlist.addAll(newItems)
        currentPlaylistSignature = buildPlaylistSignature(newItems)
        pendingPlaylist = null
        pendingPlaylistSignature = null
        iniciarWatchDePlaylist(codigoConteudo)

        // Disparar pré-cache em background (não bloqueia reprodução)
        downloadManager.preCachePlaylist(newItems)

        // Marcar cache como pronto no banco (simplificado: após iniciar pré-cache)
        currentCodigo?.let { codigo ->
            lifecycleScope.launch(Dispatchers.IO) {
                val service = SupabaseDeviceService()
                service.atualizarStatusCache(codigo, cached = true)
            }
        }

        if (forceRestart || currentIndex >= playlist.size) {
            currentIndex = 0
            playLoop()
        }
    }

    private fun iniciarWatchDePlaylist(codigoConteudo: String) {
        playlistWatchJob?.cancel()

        val service = PlaylistService()
        playlistWatchJob = lifecycleScope.launch {
            while (isActive) {
                val novosItens = withContext(Dispatchers.IO) {
                    service.carregarPlaylist(codigoConteudo)
                }

                if (novosItens.isEmpty()) {
                    delay(3000)
                    continue
                }

                val novaAssinatura = buildPlaylistSignature(novosItens)
                if (novaAssinatura != currentPlaylistSignature) {
                    // Sempre pré-cache da nova versão imediatamente em background.
                    downloadManager.preCachePlaylist(novosItens)

                    if (playlist.isNotEmpty() && isOnlyAdditionAtEnd(playlist, novosItens)) {
                        pendingPlaylist = novosItens
                        pendingPlaylistSignature = novaAssinatura
                    } else {
                        applyPlaylistImmediately(
                            codigoConteudo = codigoConteudo,
                            newItems = novosItens,
                            forceRestart = false
                        )
                    }
                }

                delay(3000) // atualização rápida para refletir mudanças quase instantaneamente.
            }
        }
    }

    private fun playLoop() {
        if (playlist.isEmpty()) return
        currentIndex = currentIndex % playlist.size
        val item = playlist[currentIndex]

        when (item.type) {
            ItemType.VIDEO -> playVideo(item)
            ItemType.IMAGE -> showImage(item)
        }
    }

    private fun pickUrlForOrientation(item: PlaylistItem): String {
        val orientation = resources.configuration.orientation
        val isPortrait = orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
        return when {
            isPortrait && !item.urlPortrait.isNullOrBlank() -> item.urlPortrait
            !isPortrait && !item.urlLandscape.isNullOrBlank() -> item.urlLandscape
            else -> item.url
        }
    }

    private fun applyFitForVideo(item: PlaylistItem) {
        // Baseado em FIT_RULES do JS: default "cover" para tudo
        val fit = (item.fit ?: "cover").lowercase()
        when (fit) {
            "contain" -> playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            "fill" -> playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            else -> playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM // "cover"
        }
    }

    private fun applyFitForImage(item: PlaylistItem) {
        val fit = (item.fit ?: "cover").lowercase()
        imageView.scaleType = when (fit) {
            "contain" -> ImageView.ScaleType.FIT_CENTER
            "fill" -> ImageView.ScaleType.FIT_XY
            else -> ImageView.ScaleType.CENTER_CROP // "cover"
        }
    }

    private fun playVideo(item: PlaylistItem) {
        isShowingVideo = true
        hideImageOnNextVideoFrame = imageView.visibility == View.VISIBLE
        playerView.visibility = View.VISIBLE
        exoPlayer?.volume = 0f

        imageView.removeCallbacks(nextRunnable)

        val url = pickUrlForOrientation(item)
        applyFitForVideo(item)

        val mediaItem = MediaItem.fromUri(url)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
    }

    private fun showImage(item: PlaylistItem) {
        isShowingVideo = false
        hideImageOnNextVideoFrame = false
        exoPlayer?.stop()

        val duration = item.durationMs ?: 15000L

        val url = pickUrlForOrientation(item)
        applyFitForImage(item)

        imageView.load(url) {
            crossfade(true)
            listener(
                onSuccess = { _, _ ->
                    imageView.visibility = View.VISIBLE
                    playerView.visibility = View.GONE
                },
                onError = { _, _ ->
                    // Em erro, ainda escondemos vídeo para evitar frame congelado com áudio.
                    imageView.visibility = View.VISIBLE
                    playerView.visibility = View.GONE
                }
            )
        }

        imageView.removeCallbacks(nextRunnable)
        if (duration > 0L) {
            imageView.postDelayed(nextRunnable, duration)
        }
    }

    private fun nextItem() {
        if (playlist.isEmpty()) return
        currentIndex = (currentIndex + 1) % playlist.size

        // Se só adicionaram novos itens, aplicar no início do próximo ciclo.
        if (currentIndex == 0 && pendingPlaylist != null && pendingPlaylistSignature != currentPlaylistSignature) {
            val pending = pendingPlaylist ?: emptyList()
            if (pending.isNotEmpty()) {
                playlist.clear()
                playlist.addAll(pending)
                currentPlaylistSignature = pendingPlaylistSignature
            }
            pendingPlaylist = null
            pendingPlaylistSignature = null
        }

        playLoop()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageView.removeCallbacks(nextRunnable)
        exoPlayer?.release()
        exoPlayer = null
        heartbeatJob?.cancel()
        playlistWatchJob?.cancel()
        promoWatchJob?.cancel()
        deviceCommandJob?.cancel()
    }

    // ===== Promoções =====

    private fun iniciarWatchDePromocao(codigo: String) {
        if (promoWatchJob?.isActive == true) return

        val service = PromotionService()
        promoWatchJob = lifecycleScope.launch {
            while (isActive) {
                val promo = withContext(Dispatchers.IO) {
                    service.getPromotionForCodigo(codigo)
                }

                if (promo == null) {
                    // Não há promoção ativa: esconder overlay se estiver visível
                    if (promoOverlay.visibility == View.VISIBLE) {
                        promoOverlay.visibility = View.GONE
                        currentPromotion = null
                    }
                } else {
                    // Há promoção ativa
                    if (currentPromotion == null) {
                        // Abrir overlay pela primeira vez
                        currentPromotion = promo
                        mostrarPromocao(promo)
                    } else if (currentPromotion?.idPromo == promo.idPromo) {
                        // Atualizar contador/textos se mudou
                        if (promo.contador != currentPromotion?.contador ||
                            promo.texto != currentPromotion?.texto ||
                            promo.valorAntes != currentPromotion?.valorAntes ||
                            promo.valorPromo != currentPromotion?.valorPromo
                        ) {
                            currentPromotion = promo
                            atualizarViewsPromocao(promo)
                        }
                    } else {
                        // Promo diferente: substituir
                        currentPromotion = promo
                        mostrarPromocao(promo)
                    }

                    // Se contador chegou a zero, desativar (seguindo lógica do JS)
                    if (promo.contador <= 0) {
                        withContext(Dispatchers.IO) {
                            service.desativarPromocao(codigo, promo.idPromo)
                        }
                        currentPromotion = null
                        promoOverlay.visibility = View.GONE
                    }
                }

                delay(2000) // Verificação rápida para refletir promo quase instantaneamente.
            }
        }
    }

    private fun mostrarPromocao(promo: PromotionData) {
        atualizarViewsPromocao(promo)
        promoOverlay.visibility = View.VISIBLE
    }

    private fun atualizarViewsPromocao(promo: PromotionData) {
        // Imagem
        if (!promo.imagemUrl.isNullOrBlank()) {
            promoImage.visibility = View.VISIBLE
            promoImage.load(promo.imagemUrl) {
                crossfade(true)
            }
        } else {
            promoImage.visibility = View.GONE
        }

        // Texto
        promoText.text = promo.texto ?: "Promoção especial"

        // Preços (formatar em pt-BR semelhante ao JS)
        promoOriginalPrice.text = formatarValorMonetario(promo.valorAntes)
        promoOriginalPrice.paintFlags = promoOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        promoPrice.text = formatarValorMonetario(promo.valorPromo)

        // Contador
        promoCounterView.text = promo.contador.toString()
    }

    private fun formatarValorMonetario(valor: String?): String {
        if (valor.isNullOrBlank()) return ""
        return try {
            // Tentar tratar como número em reais (pode vir em centavos ou já formatado)
            if (valor.contains(",")) {
                "R$ $valor"
            } else {
                val numero = valor.toDoubleOrNull() ?: return "R$ $valor"
                val locale = java.util.Locale("pt", "BR")
                val format = java.text.NumberFormat.getCurrencyInstance(locale)
                format.format(
                    if (numero >= 100 && numero % 1.0 == 0.0) numero / 100.0 else numero
                )
            }
        } catch (_: Exception) {
            "R$ $valor"
        }
    }

    private fun iniciarWatchDeComandos() {
        if (deviceCommandJob?.isActive == true) return

        val service = DeviceCommandService()
        deviceCommandJob = lifecycleScope.launch {
            while (isActive) {
                delay(10000) // 10s
                val deviceId = withContext(Dispatchers.IO) {
                    DeviceIdProvider.getDeviceId(this@PlayerActivity)
                }
                val comandos = withContext(Dispatchers.IO) {
                    service.getPendingCommands(deviceId)
                }

                for (comando in comandos) {
                    try {
                        when (comando.command) {
                            "restart_app" -> {
                                // Marcar como executado antes de reiniciar
                                withContext(Dispatchers.IO) {
                                    service.markExecuted(comando.id)
                                }
                                // Reiniciar a Activity (equivalente a location.reload() no JS)
                                runOnUiThread {
                                    recreate()
                                }
                                return@launch // Sair após processar restart
                            }
                            "stop_player" -> {
                                withContext(Dispatchers.IO) {
                                    service.markExecuted(comando.id)
                                }
                                pararTudoMostrarLogin(limparCodigoSalvo = true)
                                return@launch
                            }
                            else -> {
                                // Outros comandos podem ser adicionados aqui
                                // Marcar como executado mesmo assim
                                withContext(Dispatchers.IO) {
                                    service.markExecuted(comando.id)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Ignorar erros ao processar comandos
                    }
                }
            }
        }
    }

    private fun limparCodigoLocal() {
        val prefs = getSharedPreferences("mrit_prefs", MODE_PRIVATE)
        prefs.edit().remove("display_codigo").apply()
    }

    private fun pararTudoMostrarLogin(limparCodigoSalvo: Boolean) {
        imageView.removeCallbacks(nextRunnable)
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        imageView.setImageDrawable(null)
        playlist.clear()
        currentIndex = 0
        currentPlaylistSignature = null
        currentPromotion = null
        promoOverlay.visibility = View.GONE

        if (limparCodigoSalvo) {
            limparCodigoLocal()
            codigoEditText.setText("")
        }

        aplicarEstadoSemCodigo()
    }

    private fun isDisplayLockStale(deviceLastSeen: String?): Boolean {
        if (deviceLastSeen.isNullOrBlank()) return false
        return try {
            val lastSeen = Instant.parse(deviceLastSeen)
            Duration.between(lastSeen, Instant.now()).seconds > 120
        } catch (_: Exception) {
            false
        }
    }
}

