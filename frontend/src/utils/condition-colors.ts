const PART_CONDITION_COLORS: Record<string, string> = {
  NEW: 'bg-green-100 text-green-800',
  EXCELLENT: 'bg-blue-100 text-blue-800',
  GOOD: 'bg-yellow-100 text-yellow-800',
  FAIR: 'bg-orange-100 text-orange-800',
  POOR: 'bg-red-100 text-red-800'
}

const RECORD_CONDITION_COLORS: Record<string, string> = {
  MINT: 'bg-green-100 text-green-800',
  NEAR_MINT: 'bg-emerald-100 text-emerald-800',
  EXCELLENT: 'bg-blue-100 text-blue-800',
  VERY_GOOD: 'bg-purple-100 text-purple-800',
  GOOD: 'bg-yellow-100 text-yellow-800',
  FAIR: 'bg-orange-100 text-orange-800',
  POOR: 'bg-red-100 text-red-800'
}

export function getPartConditionColor(condition: string): string {
  return PART_CONDITION_COLORS[condition] || 'bg-gray-100 text-gray-800'
}

export function getRecordConditionColor(condition: string): string {
  return RECORD_CONDITION_COLORS[condition] || 'bg-gray-100 text-gray-800'
}
