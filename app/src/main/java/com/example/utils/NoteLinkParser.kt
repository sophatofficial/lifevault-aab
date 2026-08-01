package com.example.utils

import com.example.data.VaultItem
import kotlin.math.cos
import kotlin.math.sin

data class GraphConnectionNode(
    val id: Long,
    val title: String,
    val category: String,
    val notebook: String,
    val outgoingLinks: List<String>,
    val backlinks: List<String>,
    val connectedTitles: List<String>,
    val xRatio: Float,
    val yRatio: Float,
    val vaultItem: VaultItem?
)

object NoteLinkParser {
    private val LINK_REGEX = Regex("""\[\[(.*?)\]\]""")

    /**
     * Extracts all note titles referenced using the [[Note Title]] syntax.
     */
    fun extractInternalLinks(content: String): List<String> {
        if (content.isBlank()) return emptyList()
        return LINK_REGEX.findAll(content)
            .map { match ->
                val raw = match.groupValues[1]
                // Handle [[Title|Alias]] pipe syntax if present
                raw.split("|")[0].trim()
            }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()
    }

    /**
     * Builds a bi-directional knowledge graph from a list of VaultItems.
     */
    fun buildKnowledgeGraph(items: List<VaultItem>): List<GraphConnectionNode> {
        if (items.isEmpty()) return emptyList()

        // Map note titles to VaultItem
        val titleToItemMap = items.associateBy { it.title.trim().lowercase() }

        // Step 1: Compute outgoing links for each note
        val outgoingMap = mutableMapOf<Long, MutableSet<String>>()
        val backlinksMap = mutableMapOf<String, MutableSet<String>>() // target lowerTitle -> set of source titles

        items.forEach { item ->
            val links = extractInternalLinks(item.content)
            outgoingMap[item.id] = links.toMutableSet()

            links.forEach { targetTitle ->
                val lowerTarget = targetTitle.trim().lowercase()
                backlinksMap.getOrPut(lowerTarget) { mutableSetOf() }.add(item.title)
            }
        }

        // Step 2: Build graph nodes with automatic spatial circular layout
        val count = items.size
        val angleStep = (2 * Math.PI) / count.coerceAtLeast(1)
        val radiusRatio = 0.32f
        val centerX = 0.5f
        val centerY = 0.5f

        return items.mapIndexed { index, item ->
            val angle = index * angleStep
            val x = centerX + (radiusRatio * cos(angle)).toFloat()
            val y = centerY + (radiusRatio * sin(angle)).toFloat()

            val outgoing = outgoingMap[item.id]?.toList() ?: emptyList()
            val backlinks = backlinksMap[item.title.trim().lowercase()]?.toList() ?: emptyList()

            // Bi-directional connections = outgoing + backlinks
            val connectedTitles = (outgoing + backlinks).distinct()

            GraphConnectionNode(
                id = item.id,
                title = item.title,
                category = item.category,
                notebook = item.notebook,
                outgoingLinks = outgoing,
                backlinks = backlinks,
                connectedTitles = connectedTitles,
                xRatio = x.coerceIn(0.12f, 0.88f),
                yRatio = y.coerceIn(0.15f, 0.85f),
                vaultItem = item
            )
        }
    }
}
