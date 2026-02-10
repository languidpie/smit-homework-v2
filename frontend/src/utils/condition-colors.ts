const PART_CONDITION_COLORS: Record<string, string> = {
  NEW: 'badge-green',
  EXCELLENT: 'badge-blue',
  GOOD: 'badge-yellow',
  FAIR: 'bg-orange-100 text-orange-800',
  POOR: 'badge-red'
}

const RECORD_CONDITION_COLORS: Record<string, string> = {
  MINT: 'badge-green',
  NEAR_MINT: 'bg-emerald-100 text-emerald-800',
  EXCELLENT: 'badge-blue',
  VERY_GOOD: 'bg-purple-100 text-purple-800',
  GOOD: 'badge-yellow',
  FAIR: 'bg-orange-100 text-orange-800',
  POOR: 'badge-red'
}

export function getPartConditionColor(condition: string): string {
  return PART_CONDITION_COLORS[condition] || 'badge-gray'
}

export function getRecordConditionColor(condition: string): string {
  return RECORD_CONDITION_COLORS[condition] || 'badge-gray'
}
