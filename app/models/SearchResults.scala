package models

/**
 * Search Results
 */
case class SearchResults[T] (
  elements: List[T],
  total: Int
)

object SearchResults {}
