export const COUNTRY_OPTIONS = [
  { label: '中国', value: 'CN' },
  { label: '美国', value: 'US' },
  { label: '新加坡', value: 'SG' },
  { label: '德国', value: 'DE' },
  { label: '阿联酋', value: 'AE' }
]

export function getCountryLabel(countryCode: string) {
  return COUNTRY_OPTIONS.find((item) => item.value === countryCode)?.label ?? countryCode
}
